package com.opengamma.strata.math.impl.interpolation;

import org.testng.annotations.Test;

import com.opengamma.strata.collect.array.DoubleArray;

/**
 * Test {@link SmithWilsonCurveFunction}.
 */
@Test
public class SmithWilsonCurveFunctionTest {

  public void test() {
    DoubleArray weights = DoubleArray.of(
        151.831920686776, -74.9406108441653
//        20.5624693064541, -17.2347807832613, 19.4233972057639, -21.1997070125888, 11.5259270035871, -6.326871758593,
//        6.00032585336422, -1.69452778333647, -8.52830212521675, 12.3940113388464,
//        -1.87303343019072, -6.91696358985065, 4.46137187020777, 2.22045637369195, -5.70937927466433E-02, -17.1831196410873,
//        40.4592421328953, -55.674200952991, 45.5276226307343, -15.8190044974401
    );
    DoubleArray nodes = DoubleArray.of(
        1d, 2d
//        1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d, 9d, 10d,
//        11d, 12d, 13d, 14d, 15d, 16d, 17d, 18d, 19d, 20d
    );
//    double alpha = 0.140959;
    double alpha = 0.070642;

    for (int i = 0; i < 121; ++i) {
      double t = (double) i;
      double df = SmithWilsonCurveFunction.DEFAULT.discountFactor(t, alpha, nodes, weights);
//      System.out.println(t + "\t" + df + "\t" + -Math.log(df) / t + "\t" + (Math.pow(df, -1d / t) - 1d));
    }
  }

}
