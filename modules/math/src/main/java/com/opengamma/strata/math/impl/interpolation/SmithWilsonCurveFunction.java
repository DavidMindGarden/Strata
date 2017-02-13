package com.opengamma.strata.math.impl.interpolation;

import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.collect.array.DoubleArray;
import com.opengamma.strata.math.impl.FunctionUtils;

public class SmithWilsonCurveFunction {

  // TODO default ultimate forward rate

  public static final SmithWilsonCurveFunction DEFAULT = SmithWilsonCurveFunction.of(0.042); // eurozone

  private final double ufr;

  public static SmithWilsonCurveFunction of(double ccUfr) {
    return new SmithWilsonCurveFunction(ccUfr);
  }

  private SmithWilsonCurveFunction(double ccUfr) {
    this.ufr = Math.log(1d + ccUfr);
  }

  public double discountFactor(double t, double alpha, DoubleArray nodes, DoubleArray weights) {
    int size = nodes.size();
    ArgChecker.isTrue(size == weights.size(), "nodes and weights must be the same size");
    // TODO ascending order
    
    double res = Math.exp(-ufr * t);
    int lowerBound = FunctionUtils.getLowerBoundIndex(nodes, t);
//    for (int i = 0; i < lowerBound + 1; ++i) {
//      res += weights.get(i) * wilsonFunctionLeft(t, alpha, nodes.get(i));
//    }
//    for (int i = lowerBound + 1; i < size; ++i) {
//      res += weights.get(i) * wilsonFunctionRight(t, alpha, nodes.get(i));
//    }

    for (int i = 0; i < size; ++i) {
      res += t < nodes.get(i) ? weights.get(i) * wilsonFunctionRight(t, alpha, nodes.get(i))
          : weights.get(i) * wilsonFunctionLeft(t, alpha, nodes.get(i));
    }

    return res;
  }

  // TODO value and sensitivity?

  public DoubleArray discountFactorWeightSensitivity(double t, double alpha, DoubleArray nodes, DoubleArray weights) {
    int size = nodes.size();
    ArgChecker.isTrue(size == weights.size(), "nodes and weights must be the same size");
    // TODO ascending order

    double[] res = new double[size];
    int lowerBound = FunctionUtils.getLowerBoundIndex(nodes, t);
    for (int i = 0; i < lowerBound; ++i) {
      res[i] = wilsonFunctionRight(t, alpha, nodes.get(i));
    }
    for (int i = lowerBound; i < size; ++i) {
      res[i] = wilsonFunctionLeft(t, alpha, nodes.get(i));
    }
    return DoubleArray.ofUnsafe(res);
  }

  // t < node
  private double wilsonFunctionRight(double t, double alpha, double node) {
    double alphaT = alpha * t;
//    return Math.exp(-ufr * (t + node)) * (alphaT - Math.exp(-alpha * node) * Math.sinh(alphaT));
    return Math.exp(-ufr * (t)) * (alphaT - Math.exp(-alpha * node) * Math.sinh(alphaT));
  }

  // t > node
  private double wilsonFunctionLeft(double t, double alpha, double node) {
    double alphaNode = alpha * node;
//    return Math.exp(-ufr * (t + node)) * (alphaNode - Math.exp(-alpha * t) * Math.sinh(alphaNode));
    return Math.exp(-ufr * (t)) * (alphaNode - Math.exp(-alpha * t) * Math.sinh(alphaNode));
  }

}
