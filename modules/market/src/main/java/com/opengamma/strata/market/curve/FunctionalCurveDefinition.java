/**
 * Copyright (C) 2017 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.market.curve;

import static com.opengamma.strata.collect.Guavate.toImmutableList;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.stream.Collectors.toCollection;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.google.common.collect.ImmutableList;
import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.basics.date.DayCount;
import com.opengamma.strata.collect.Messages;
import com.opengamma.strata.collect.array.DoubleArray;
import com.opengamma.strata.collect.tuple.Pair;
import com.opengamma.strata.market.ValueType;
import com.opengamma.strata.market.param.DatedParameterMetadata;

/**
 * TODO javadoc
 */
@BeanDefinition
public final class FunctionalCurveDefinition
    implements CurveDefinition, ImmutableBean, Serializable {

  /**
   * The curve name.
   */
  @PropertyDefinition(validate = "notNull", overrideGet = true)
  private final CurveName name;
  /**
   * The x-value type, providing meaning to the x-values of the curve.
   * <p>
   * This type provides meaning to the x-values. For example, the x-value might
   * represent a year fraction, as represented using {@link ValueType#YEAR_FRACTION}.
   * <p>
   * If using the builder, this defaults to {@link ValueType#UNKNOWN}.
   */
  @PropertyDefinition(validate = "notNull")
  private final ValueType xValueType;
  /**
   * The y-value type, providing meaning to the y-values of the curve.
   * <p>
   * This type provides meaning to the y-values. For example, the y-value might
   * represent a zero rate, as represented using {@link ValueType#ZERO_RATE}.
   * <p>
   * If using the builder, this defaults to {@link ValueType#UNKNOWN}.
   */
  @PropertyDefinition(validate = "notNull", overrideGet = true)
  private final ValueType yValueType;
  /**
   * The day count, optional.
   * <p>
   * If the x-value of the curve represents time as a year fraction, the day count
   * can be specified to define how the year fraction is calculated.
   */
  @PropertyDefinition(get = "optional")
  private final DayCount dayCount;
  /**
   * The nodes in the curve.
   * <p>
   * The nodes are used to find the par rates and calibrate the curve.
   * There must be at least two nodes in the curve.
   */
  @PropertyDefinition(validate = "notNull", builderType = "List<? extends CurveNode>", overrideGet = true)
  private final ImmutableList<CurveNode> nodes;

  @PropertyDefinition(validate = "notNull")
  private final DoubleArray initialGuess;

  @PropertyDefinition(validate = "notNull")
  private final BiFunction<DoubleArray, Double, Double> valueFunction;

  @PropertyDefinition(validate = "notNull")
  private final BiFunction<DoubleArray, Double, Double> derivativeFunction;

  @PropertyDefinition(validate = "notNull")
  private final BiFunction<DoubleArray, Double, DoubleArray> sensitivityFunction;

  //-------------------------------------------------------------------------
  @Override
  public FunctionalCurveDefinition filtered(LocalDate valuationDate, ReferenceData refData) {
    // mutable list of date-node pairs
    ArrayList<Pair<LocalDate, CurveNode>> nodeDates = nodes.stream()
        .map(node -> Pair.of(node.date(valuationDate, refData), node))
        .collect(toCollection(ArrayList::new));
    // delete nodes if clash, but don't throw exceptions yet
    loop:
    for (int i = 0; i < nodeDates.size(); i++) {
      Pair<LocalDate, CurveNode> pair = nodeDates.get(i);
      CurveNodeDateOrder restriction = pair.getSecond().getDateOrder();
      // compare node to previous node
      if (i > 0) {
        Pair<LocalDate, CurveNode> pairBefore = nodeDates.get(i - 1);
        if (DAYS.between(pairBefore.getFirst(), pair.getFirst()) < restriction.getMinGapInDays()) {
          switch (restriction.getAction()) {
            case DROP_THIS:
              nodeDates.remove(i);
              i = -1;  // restart loop
              continue loop;
            case DROP_OTHER:
              nodeDates.remove(i - 1);
              i = -1;  // restart loop
              continue loop;
            case EXCEPTION:
              break;  // do nothing yet
          }
        }
      }
      // compare node to next node
      if (i < nodeDates.size() - 1) {
        Pair<LocalDate, CurveNode> pairAfter = nodeDates.get(i + 1);
        if (DAYS.between(pair.getFirst(), pairAfter.getFirst()) < restriction.getMinGapInDays()) {
          switch (restriction.getAction()) {
            case DROP_THIS:
              nodeDates.remove(i);
              i = -1;  // restart loop
              continue loop;
            case DROP_OTHER:
              nodeDates.remove(i + 1);
              i = -1;  // restart loop
              continue loop;
            case EXCEPTION:
              break;  // do nothing yet
          }
        }
      }
    }
    // throw exceptions if rules breached
    for (int i = 0; i < nodeDates.size(); i++) {
      Pair<LocalDate, CurveNode> pair = nodeDates.get(i);
      CurveNodeDateOrder restriction = pair.getSecond().getDateOrder();
      // compare node to previous node
      if (i > 0) {
        Pair<LocalDate, CurveNode> pairBefore = nodeDates.get(i - 1);
        if (DAYS.between(pairBefore.getFirst(), pair.getFirst()) < restriction.getMinGapInDays()) {
          throw new IllegalArgumentException(Messages.format(
              "Curve node dates clash, node '{}' and '{}' resolved to dates '{}' and '{}' respectively",
              pairBefore.getSecond().getLabel(),
              pair.getSecond().getLabel(),
              pairBefore.getFirst(),
              pair.getFirst()));
        }
      }
      // compare node to next node
      if (i < nodeDates.size() - 1) {
        Pair<LocalDate, CurveNode> pairAfter = nodeDates.get(i + 1);
        if (DAYS.between(pair.getFirst(), pairAfter.getFirst()) < restriction.getMinGapInDays()) {
          throw new IllegalArgumentException(Messages.format(
              "Curve node dates clash, node '{}' and '{}' resolved to dates '{}' and '{}' respectively",
              pair.getSecond().getLabel(),
              pairAfter.getSecond().getLabel(),
              pair.getFirst(),
              pairAfter.getFirst()));
        }
      }
    }
    // return the resolved definition
    List<CurveNode> filteredNodes = nodeDates.stream().map(p -> p.getSecond()).collect(toImmutableList());
    return new FunctionalCurveDefinition(
        name,
        xValueType,
        yValueType,
        dayCount,
        filteredNodes,
        initialGuess,
        valueFunction,
        derivativeFunction,
        sensitivityFunction);
  }

  @Override
  public CurveMetadata metadata(LocalDate valuationDate, ReferenceData refData) {
    List<DatedParameterMetadata> nodeMetadata = nodes.stream()
        .map(node -> node.metadata(valuationDate, refData))
        .collect(toImmutableList());
    return DefaultCurveMetadata.builder()
        .curveName(name)
        .xValueType(xValueType)
        .yValueType(yValueType)
        .dayCount(dayCount)
        .parameterMetadata(nodeMetadata)
        .build();
  }

  @Override
  public ParameterizedFunctionalCurve curve(LocalDate valuationDate, CurveMetadata metadata, DoubleArray parameters) {
    return ParameterizedFunctionalCurve.of(
        metadata,
        parameters,
        valueFunction,
        derivativeFunction,
        sensitivityFunction);
  }

  // builds node times from node dates
  private DoubleArray buildNodeTimes(LocalDate valuationDate, CurveMetadata metadata) {
    if (metadata.getXValueType().equals(ValueType.YEAR_FRACTION)) {
      return DoubleArray.of(getParameterCount(), i -> {
        LocalDate nodeDate = ((DatedParameterMetadata) metadata.getParameterMetadata().get().get(i)).getDate();
        return getDayCount().get().yearFraction(valuationDate, nodeDate);
      });

    } else {
      throw new IllegalArgumentException("Metadata XValueType should be YearFraction in curve definition");
    }
  }

  @Override
  public int getParameterCount() {
    return initialGuess.size();
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code FunctionalCurveDefinition}.
   * @return the meta-bean, not null
   */
  public static FunctionalCurveDefinition.Meta meta() {
    return FunctionalCurveDefinition.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(FunctionalCurveDefinition.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static FunctionalCurveDefinition.Builder builder() {
    return new FunctionalCurveDefinition.Builder();
  }

  private FunctionalCurveDefinition(
      CurveName name,
      ValueType xValueType,
      ValueType yValueType,
      DayCount dayCount,
      List<? extends CurveNode> nodes,
      DoubleArray initialGuess,
      BiFunction<DoubleArray, Double, Double> valueFunction,
      BiFunction<DoubleArray, Double, Double> derivativeFunction,
      BiFunction<DoubleArray, Double, DoubleArray> sensitivityFunction) {
    JodaBeanUtils.notNull(name, "name");
    JodaBeanUtils.notNull(xValueType, "xValueType");
    JodaBeanUtils.notNull(yValueType, "yValueType");
    JodaBeanUtils.notNull(nodes, "nodes");
    JodaBeanUtils.notNull(initialGuess, "initialGuess");
    JodaBeanUtils.notNull(valueFunction, "valueFunction");
    JodaBeanUtils.notNull(derivativeFunction, "derivativeFunction");
    JodaBeanUtils.notNull(sensitivityFunction, "sensitivityFunction");
    this.name = name;
    this.xValueType = xValueType;
    this.yValueType = yValueType;
    this.dayCount = dayCount;
    this.nodes = ImmutableList.copyOf(nodes);
    this.initialGuess = initialGuess;
    this.valueFunction = valueFunction;
    this.derivativeFunction = derivativeFunction;
    this.sensitivityFunction = sensitivityFunction;
  }

  @Override
  public FunctionalCurveDefinition.Meta metaBean() {
    return FunctionalCurveDefinition.Meta.INSTANCE;
  }

  @Override
  public <R> Property<R> property(String propertyName) {
    return metaBean().<R>metaProperty(propertyName).createProperty(this);
  }

  @Override
  public Set<String> propertyNames() {
    return metaBean().metaPropertyMap().keySet();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the curve name.
   * @return the value of the property, not null
   */
  @Override
  public CurveName getName() {
    return name;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the x-value type, providing meaning to the x-values of the curve.
   * <p>
   * This type provides meaning to the x-values. For example, the x-value might
   * represent a year fraction, as represented using {@link ValueType#YEAR_FRACTION}.
   * <p>
   * If using the builder, this defaults to {@link ValueType#UNKNOWN}.
   * @return the value of the property, not null
   */
  public ValueType getXValueType() {
    return xValueType;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the y-value type, providing meaning to the y-values of the curve.
   * <p>
   * This type provides meaning to the y-values. For example, the y-value might
   * represent a zero rate, as represented using {@link ValueType#ZERO_RATE}.
   * <p>
   * If using the builder, this defaults to {@link ValueType#UNKNOWN}.
   * @return the value of the property, not null
   */
  @Override
  public ValueType getYValueType() {
    return yValueType;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the day count, optional.
   * <p>
   * If the x-value of the curve represents time as a year fraction, the day count
   * can be specified to define how the year fraction is calculated.
   * @return the optional value of the property, not null
   */
  public Optional<DayCount> getDayCount() {
    return Optional.ofNullable(dayCount);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the nodes in the curve.
   * <p>
   * The nodes are used to find the par rates and calibrate the curve.
   * There must be at least two nodes in the curve.
   * @return the value of the property, not null
   */
  @Override
  public ImmutableList<CurveNode> getNodes() {
    return nodes;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the initialGuess.
   * @return the value of the property, not null
   */
  public DoubleArray getInitialGuess() {
    return initialGuess;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the valueFunction.
   * @return the value of the property, not null
   */
  public BiFunction<DoubleArray, Double, Double> getValueFunction() {
    return valueFunction;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the derivativeFunction.
   * @return the value of the property, not null
   */
  public BiFunction<DoubleArray, Double, Double> getDerivativeFunction() {
    return derivativeFunction;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the sensitivityFunction.
   * @return the value of the property, not null
   */
  public BiFunction<DoubleArray, Double, DoubleArray> getSensitivityFunction() {
    return sensitivityFunction;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      FunctionalCurveDefinition other = (FunctionalCurveDefinition) obj;
      return JodaBeanUtils.equal(name, other.name) &&
          JodaBeanUtils.equal(xValueType, other.xValueType) &&
          JodaBeanUtils.equal(yValueType, other.yValueType) &&
          JodaBeanUtils.equal(dayCount, other.dayCount) &&
          JodaBeanUtils.equal(nodes, other.nodes) &&
          JodaBeanUtils.equal(initialGuess, other.initialGuess) &&
          JodaBeanUtils.equal(valueFunction, other.valueFunction) &&
          JodaBeanUtils.equal(derivativeFunction, other.derivativeFunction) &&
          JodaBeanUtils.equal(sensitivityFunction, other.sensitivityFunction);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(name);
    hash = hash * 31 + JodaBeanUtils.hashCode(xValueType);
    hash = hash * 31 + JodaBeanUtils.hashCode(yValueType);
    hash = hash * 31 + JodaBeanUtils.hashCode(dayCount);
    hash = hash * 31 + JodaBeanUtils.hashCode(nodes);
    hash = hash * 31 + JodaBeanUtils.hashCode(initialGuess);
    hash = hash * 31 + JodaBeanUtils.hashCode(valueFunction);
    hash = hash * 31 + JodaBeanUtils.hashCode(derivativeFunction);
    hash = hash * 31 + JodaBeanUtils.hashCode(sensitivityFunction);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(320);
    buf.append("FunctionalCurveDefinition{");
    buf.append("name").append('=').append(name).append(',').append(' ');
    buf.append("xValueType").append('=').append(xValueType).append(',').append(' ');
    buf.append("yValueType").append('=').append(yValueType).append(',').append(' ');
    buf.append("dayCount").append('=').append(dayCount).append(',').append(' ');
    buf.append("nodes").append('=').append(nodes).append(',').append(' ');
    buf.append("initialGuess").append('=').append(initialGuess).append(',').append(' ');
    buf.append("valueFunction").append('=').append(valueFunction).append(',').append(' ');
    buf.append("derivativeFunction").append('=').append(derivativeFunction).append(',').append(' ');
    buf.append("sensitivityFunction").append('=').append(JodaBeanUtils.toString(sensitivityFunction));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code FunctionalCurveDefinition}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code name} property.
     */
    private final MetaProperty<CurveName> name = DirectMetaProperty.ofImmutable(
        this, "name", FunctionalCurveDefinition.class, CurveName.class);
    /**
     * The meta-property for the {@code xValueType} property.
     */
    private final MetaProperty<ValueType> xValueType = DirectMetaProperty.ofImmutable(
        this, "xValueType", FunctionalCurveDefinition.class, ValueType.class);
    /**
     * The meta-property for the {@code yValueType} property.
     */
    private final MetaProperty<ValueType> yValueType = DirectMetaProperty.ofImmutable(
        this, "yValueType", FunctionalCurveDefinition.class, ValueType.class);
    /**
     * The meta-property for the {@code dayCount} property.
     */
    private final MetaProperty<DayCount> dayCount = DirectMetaProperty.ofImmutable(
        this, "dayCount", FunctionalCurveDefinition.class, DayCount.class);
    /**
     * The meta-property for the {@code nodes} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ImmutableList<CurveNode>> nodes = DirectMetaProperty.ofImmutable(
        this, "nodes", FunctionalCurveDefinition.class, (Class) ImmutableList.class);
    /**
     * The meta-property for the {@code initialGuess} property.
     */
    private final MetaProperty<DoubleArray> initialGuess = DirectMetaProperty.ofImmutable(
        this, "initialGuess", FunctionalCurveDefinition.class, DoubleArray.class);
    /**
     * The meta-property for the {@code valueFunction} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<BiFunction<DoubleArray, Double, Double>> valueFunction = DirectMetaProperty.ofImmutable(
        this, "valueFunction", FunctionalCurveDefinition.class, (Class) BiFunction.class);
    /**
     * The meta-property for the {@code derivativeFunction} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<BiFunction<DoubleArray, Double, Double>> derivativeFunction = DirectMetaProperty.ofImmutable(
        this, "derivativeFunction", FunctionalCurveDefinition.class, (Class) BiFunction.class);
    /**
     * The meta-property for the {@code sensitivityFunction} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<BiFunction<DoubleArray, Double, DoubleArray>> sensitivityFunction = DirectMetaProperty.ofImmutable(
        this, "sensitivityFunction", FunctionalCurveDefinition.class, (Class) BiFunction.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "name",
        "xValueType",
        "yValueType",
        "dayCount",
        "nodes",
        "initialGuess",
        "valueFunction",
        "derivativeFunction",
        "sensitivityFunction");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return name;
        case -868509005:  // xValueType
          return xValueType;
        case -1065022510:  // yValueType
          return yValueType;
        case 1905311443:  // dayCount
          return dayCount;
        case 104993457:  // nodes
          return nodes;
        case -431632141:  // initialGuess
          return initialGuess;
        case 636119145:  // valueFunction
          return valueFunction;
        case 1663351423:  // derivativeFunction
          return derivativeFunction;
        case -1353652329:  // sensitivityFunction
          return sensitivityFunction;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public FunctionalCurveDefinition.Builder builder() {
      return new FunctionalCurveDefinition.Builder();
    }

    @Override
    public Class<? extends FunctionalCurveDefinition> beanType() {
      return FunctionalCurveDefinition.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code name} property.
     * @return the meta-property, not null
     */
    public MetaProperty<CurveName> name() {
      return name;
    }

    /**
     * The meta-property for the {@code xValueType} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ValueType> xValueType() {
      return xValueType;
    }

    /**
     * The meta-property for the {@code yValueType} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ValueType> yValueType() {
      return yValueType;
    }

    /**
     * The meta-property for the {@code dayCount} property.
     * @return the meta-property, not null
     */
    public MetaProperty<DayCount> dayCount() {
      return dayCount;
    }

    /**
     * The meta-property for the {@code nodes} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ImmutableList<CurveNode>> nodes() {
      return nodes;
    }

    /**
     * The meta-property for the {@code initialGuess} property.
     * @return the meta-property, not null
     */
    public MetaProperty<DoubleArray> initialGuess() {
      return initialGuess;
    }

    /**
     * The meta-property for the {@code valueFunction} property.
     * @return the meta-property, not null
     */
    public MetaProperty<BiFunction<DoubleArray, Double, Double>> valueFunction() {
      return valueFunction;
    }

    /**
     * The meta-property for the {@code derivativeFunction} property.
     * @return the meta-property, not null
     */
    public MetaProperty<BiFunction<DoubleArray, Double, Double>> derivativeFunction() {
      return derivativeFunction;
    }

    /**
     * The meta-property for the {@code sensitivityFunction} property.
     * @return the meta-property, not null
     */
    public MetaProperty<BiFunction<DoubleArray, Double, DoubleArray>> sensitivityFunction() {
      return sensitivityFunction;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return ((FunctionalCurveDefinition) bean).getName();
        case -868509005:  // xValueType
          return ((FunctionalCurveDefinition) bean).getXValueType();
        case -1065022510:  // yValueType
          return ((FunctionalCurveDefinition) bean).getYValueType();
        case 1905311443:  // dayCount
          return ((FunctionalCurveDefinition) bean).dayCount;
        case 104993457:  // nodes
          return ((FunctionalCurveDefinition) bean).getNodes();
        case -431632141:  // initialGuess
          return ((FunctionalCurveDefinition) bean).getInitialGuess();
        case 636119145:  // valueFunction
          return ((FunctionalCurveDefinition) bean).getValueFunction();
        case 1663351423:  // derivativeFunction
          return ((FunctionalCurveDefinition) bean).getDerivativeFunction();
        case -1353652329:  // sensitivityFunction
          return ((FunctionalCurveDefinition) bean).getSensitivityFunction();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      metaProperty(propertyName);
      if (quiet) {
        return;
      }
      throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
    }

  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code FunctionalCurveDefinition}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<FunctionalCurveDefinition> {

    private CurveName name;
    private ValueType xValueType;
    private ValueType yValueType;
    private DayCount dayCount;
    private List<? extends CurveNode> nodes = ImmutableList.of();
    private DoubleArray initialGuess;
    private BiFunction<DoubleArray, Double, Double> valueFunction;
    private BiFunction<DoubleArray, Double, Double> derivativeFunction;
    private BiFunction<DoubleArray, Double, DoubleArray> sensitivityFunction;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(FunctionalCurveDefinition beanToCopy) {
      this.name = beanToCopy.getName();
      this.xValueType = beanToCopy.getXValueType();
      this.yValueType = beanToCopy.getYValueType();
      this.dayCount = beanToCopy.dayCount;
      this.nodes = beanToCopy.getNodes();
      this.initialGuess = beanToCopy.getInitialGuess();
      this.valueFunction = beanToCopy.getValueFunction();
      this.derivativeFunction = beanToCopy.getDerivativeFunction();
      this.sensitivityFunction = beanToCopy.getSensitivityFunction();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return name;
        case -868509005:  // xValueType
          return xValueType;
        case -1065022510:  // yValueType
          return yValueType;
        case 1905311443:  // dayCount
          return dayCount;
        case 104993457:  // nodes
          return nodes;
        case -431632141:  // initialGuess
          return initialGuess;
        case 636119145:  // valueFunction
          return valueFunction;
        case 1663351423:  // derivativeFunction
          return derivativeFunction;
        case -1353652329:  // sensitivityFunction
          return sensitivityFunction;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          this.name = (CurveName) newValue;
          break;
        case -868509005:  // xValueType
          this.xValueType = (ValueType) newValue;
          break;
        case -1065022510:  // yValueType
          this.yValueType = (ValueType) newValue;
          break;
        case 1905311443:  // dayCount
          this.dayCount = (DayCount) newValue;
          break;
        case 104993457:  // nodes
          this.nodes = (List<? extends CurveNode>) newValue;
          break;
        case -431632141:  // initialGuess
          this.initialGuess = (DoubleArray) newValue;
          break;
        case 636119145:  // valueFunction
          this.valueFunction = (BiFunction<DoubleArray, Double, Double>) newValue;
          break;
        case 1663351423:  // derivativeFunction
          this.derivativeFunction = (BiFunction<DoubleArray, Double, Double>) newValue;
          break;
        case -1353652329:  // sensitivityFunction
          this.sensitivityFunction = (BiFunction<DoubleArray, Double, DoubleArray>) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public Builder set(MetaProperty<?> property, Object value) {
      super.set(property, value);
      return this;
    }

    @Override
    public Builder setString(String propertyName, String value) {
      setString(meta().metaProperty(propertyName), value);
      return this;
    }

    @Override
    public Builder setString(MetaProperty<?> property, String value) {
      super.setString(property, value);
      return this;
    }

    @Override
    public Builder setAll(Map<String, ? extends Object> propertyValueMap) {
      super.setAll(propertyValueMap);
      return this;
    }

    @Override
    public FunctionalCurveDefinition build() {
      return new FunctionalCurveDefinition(
          name,
          xValueType,
          yValueType,
          dayCount,
          nodes,
          initialGuess,
          valueFunction,
          derivativeFunction,
          sensitivityFunction);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the curve name.
     * @param name  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder name(CurveName name) {
      JodaBeanUtils.notNull(name, "name");
      this.name = name;
      return this;
    }

    /**
     * Sets the x-value type, providing meaning to the x-values of the curve.
     * <p>
     * This type provides meaning to the x-values. For example, the x-value might
     * represent a year fraction, as represented using {@link ValueType#YEAR_FRACTION}.
     * <p>
     * If using the builder, this defaults to {@link ValueType#UNKNOWN}.
     * @param xValueType  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder xValueType(ValueType xValueType) {
      JodaBeanUtils.notNull(xValueType, "xValueType");
      this.xValueType = xValueType;
      return this;
    }

    /**
     * Sets the y-value type, providing meaning to the y-values of the curve.
     * <p>
     * This type provides meaning to the y-values. For example, the y-value might
     * represent a zero rate, as represented using {@link ValueType#ZERO_RATE}.
     * <p>
     * If using the builder, this defaults to {@link ValueType#UNKNOWN}.
     * @param yValueType  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder yValueType(ValueType yValueType) {
      JodaBeanUtils.notNull(yValueType, "yValueType");
      this.yValueType = yValueType;
      return this;
    }

    /**
     * Sets the day count, optional.
     * <p>
     * If the x-value of the curve represents time as a year fraction, the day count
     * can be specified to define how the year fraction is calculated.
     * @param dayCount  the new value
     * @return this, for chaining, not null
     */
    public Builder dayCount(DayCount dayCount) {
      this.dayCount = dayCount;
      return this;
    }

    /**
     * Sets the nodes in the curve.
     * <p>
     * The nodes are used to find the par rates and calibrate the curve.
     * There must be at least two nodes in the curve.
     * @param nodes  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder nodes(List<? extends CurveNode> nodes) {
      JodaBeanUtils.notNull(nodes, "nodes");
      this.nodes = nodes;
      return this;
    }

    /**
     * Sets the {@code nodes} property in the builder
     * from an array of objects.
     * @param nodes  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder nodes(CurveNode... nodes) {
      return nodes(ImmutableList.copyOf(nodes));
    }

    /**
     * Sets the initialGuess.
     * @param initialGuess  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder initialGuess(DoubleArray initialGuess) {
      JodaBeanUtils.notNull(initialGuess, "initialGuess");
      this.initialGuess = initialGuess;
      return this;
    }

    /**
     * Sets the valueFunction.
     * @param valueFunction  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder valueFunction(BiFunction<DoubleArray, Double, Double> valueFunction) {
      JodaBeanUtils.notNull(valueFunction, "valueFunction");
      this.valueFunction = valueFunction;
      return this;
    }

    /**
     * Sets the derivativeFunction.
     * @param derivativeFunction  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder derivativeFunction(BiFunction<DoubleArray, Double, Double> derivativeFunction) {
      JodaBeanUtils.notNull(derivativeFunction, "derivativeFunction");
      this.derivativeFunction = derivativeFunction;
      return this;
    }

    /**
     * Sets the sensitivityFunction.
     * @param sensitivityFunction  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder sensitivityFunction(BiFunction<DoubleArray, Double, DoubleArray> sensitivityFunction) {
      JodaBeanUtils.notNull(sensitivityFunction, "sensitivityFunction");
      this.sensitivityFunction = sensitivityFunction;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(320);
      buf.append("FunctionalCurveDefinition.Builder{");
      buf.append("name").append('=').append(JodaBeanUtils.toString(name)).append(',').append(' ');
      buf.append("xValueType").append('=').append(JodaBeanUtils.toString(xValueType)).append(',').append(' ');
      buf.append("yValueType").append('=').append(JodaBeanUtils.toString(yValueType)).append(',').append(' ');
      buf.append("dayCount").append('=').append(JodaBeanUtils.toString(dayCount)).append(',').append(' ');
      buf.append("nodes").append('=').append(JodaBeanUtils.toString(nodes)).append(',').append(' ');
      buf.append("initialGuess").append('=').append(JodaBeanUtils.toString(initialGuess)).append(',').append(' ');
      buf.append("valueFunction").append('=').append(JodaBeanUtils.toString(valueFunction)).append(',').append(' ');
      buf.append("derivativeFunction").append('=').append(JodaBeanUtils.toString(derivativeFunction)).append(',').append(' ');
      buf.append("sensitivityFunction").append('=').append(JodaBeanUtils.toString(sensitivityFunction));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
