import org.apache.mahout.math._

val drmData = drmParallelize(dense(
  (2, 2, 10.5, 10, 29.509541),  // Apple Cinnamon Cheerios
  (1, 2, 12,   12, 18.042851),  // Cap'n'Crunch
  (1, 1, 12,   13, 22.736446),  // Cocoa Puffs
  (2, 1, 11,   13, 32.207582),  // Froot Loops
  (1, 2, 12,   11, 21.871292),  // Honey Graham Ohs
  (2, 1, 16,   8,  36.187559),  // Wheaties Honey Gold
  (6, 2, 17,   1,  50.764999),  // Cheerios
  (3, 2, 13,   7,  40.400208),  // Clusters
  (3, 3, 13,   4,  45.811716)), // Great Grains Pecan
  numPartitions = 2);

// Linear regression described by y = Xb + e

// Extract feature matrix X and target variables y
val drmX = drmData(::, 0 until 4)
val y = drmData.collect(::, 4)

// Estimate beta with b ~ (X.t %*% X)^(-1) %*% X.t %*% y
val drmXtX = drmX.t %*% drmX
val drmXty = drmX.t %*% y
val XtX = drmXtX.collect
val Xty = drmXty.collect(::, 0)
val beta = solve(XtX, Xty)

def ols(drmX: DrmLike[Int], y: Vector) =
  solve(drmX.t %*% drmX, drmX.t %*% y)(::, 0)

def goodnessOfFit(drmX: DrmLike[Int], beta: Vector, y: Vector) = {
  val fittedY = (drmX %*% beta).collect(::, 0)
  (y - fittedY).norm(2)
}

// Add a bias variable
val drmXwithBiasColumn = drmX cbind 1
val betaWithBiasTerm = ols(drmXwithBiasColumn, y)
goodnessOfFit(drmXwithBiasColumn, betaWithBiasTerm, y)

// Faster with cached results
val cachedDrmX = drmXwithBiasColumn.checkpoint()
val cachedBetaWithBiasTerm = ols(cachedDrmX, y)
val goodness = goodnessOfFit(cachedDrmX, cachedBetaWithBiasTerm, y)
cachedDrmX.uncache()

