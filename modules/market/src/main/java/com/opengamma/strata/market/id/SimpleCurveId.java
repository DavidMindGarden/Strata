/**
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.market.id;

import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
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

import com.opengamma.strata.basics.market.MarketDataFeed;
import com.opengamma.strata.basics.market.MarketDataId;
import com.opengamma.strata.basics.market.MarketDataKey;
import com.opengamma.strata.basics.market.SimpleMarketDataKey;
import com.opengamma.strata.market.curve.Curve;
import com.opengamma.strata.market.curve.CurveGroupName;
import com.opengamma.strata.market.curve.CurveName;
import com.opengamma.strata.market.key.CurveKey;

/**
 * An identifier used to access a curve by name.
 * <p>
 * This is used when there is a need to obtain an instance of {@link Curve}.
 */
@BeanDefinition(builderScope = "private")
public final class SimpleCurveId
    implements SimpleMarketDataKey<Curve>, CurveKey, CurveId, ImmutableBean, Serializable {

  /**
   * The curve group name.
   */
  @PropertyDefinition(validate = "notNull", overrideGet = true)
  private final CurveGroupName curveGroupName;
  /**
   * The curve name.
   */
  @PropertyDefinition(validate = "notNull")
  private final CurveName curveName;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance used to obtain a curve by name.
   *
   * @param groupName  the curve group name
   * @param curveName  the curve name
   * @return the identifier
   */
  public static SimpleCurveId of(String groupName, String curveName) {
    return new SimpleCurveId(CurveGroupName.of(groupName), CurveName.of(curveName));
  }

  /**
   * Obtains an instance used to obtain a curve by name.
   *
   * @param groupName  the curve group name
   * @param curveName  the curve name
   * @return the identifier
   */
  public static SimpleCurveId of(CurveGroupName groupName, CurveName curveName) {
    return new SimpleCurveId(groupName, curveName);
  }

  //-------------------------------------------------------------------------
  @Override
  public Class<Curve> getMarketDataType() {
    return Curve.class;
  }

  @Override
  public MarketDataId<Curve> toMarketDataId(MarketDataFeed marketDataFeed) {
    return this;
  }

  @Override
  public MarketDataKey<Curve> toMarketDataKey() {
    return this;
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code SimpleCurveId}.
   * @return the meta-bean, not null
   */
  public static SimpleCurveId.Meta meta() {
    return SimpleCurveId.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(SimpleCurveId.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  private SimpleCurveId(
      CurveGroupName curveGroupName,
      CurveName curveName) {
    JodaBeanUtils.notNull(curveGroupName, "curveGroupName");
    JodaBeanUtils.notNull(curveName, "curveName");
    this.curveGroupName = curveGroupName;
    this.curveName = curveName;
  }

  @Override
  public SimpleCurveId.Meta metaBean() {
    return SimpleCurveId.Meta.INSTANCE;
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
   * Gets the curve group name.
   * @return the value of the property, not null
   */
  @Override
  public CurveGroupName getCurveGroupName() {
    return curveGroupName;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the curve name.
   * @return the value of the property, not null
   */
  public CurveName getCurveName() {
    return curveName;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      SimpleCurveId other = (SimpleCurveId) obj;
      return JodaBeanUtils.equal(curveGroupName, other.curveGroupName) &&
          JodaBeanUtils.equal(curveName, other.curveName);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(curveGroupName);
    hash = hash * 31 + JodaBeanUtils.hashCode(curveName);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("SimpleCurveId{");
    buf.append("curveGroupName").append('=').append(curveGroupName).append(',').append(' ');
    buf.append("curveName").append('=').append(JodaBeanUtils.toString(curveName));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code SimpleCurveId}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code curveGroupName} property.
     */
    private final MetaProperty<CurveGroupName> curveGroupName = DirectMetaProperty.ofImmutable(
        this, "curveGroupName", SimpleCurveId.class, CurveGroupName.class);
    /**
     * The meta-property for the {@code curveName} property.
     */
    private final MetaProperty<CurveName> curveName = DirectMetaProperty.ofImmutable(
        this, "curveName", SimpleCurveId.class, CurveName.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "curveGroupName",
        "curveName");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -382645893:  // curveGroupName
          return curveGroupName;
        case 771153946:  // curveName
          return curveName;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends SimpleCurveId> builder() {
      return new SimpleCurveId.Builder();
    }

    @Override
    public Class<? extends SimpleCurveId> beanType() {
      return SimpleCurveId.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code curveGroupName} property.
     * @return the meta-property, not null
     */
    public MetaProperty<CurveGroupName> curveGroupName() {
      return curveGroupName;
    }

    /**
     * The meta-property for the {@code curveName} property.
     * @return the meta-property, not null
     */
    public MetaProperty<CurveName> curveName() {
      return curveName;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -382645893:  // curveGroupName
          return ((SimpleCurveId) bean).getCurveGroupName();
        case 771153946:  // curveName
          return ((SimpleCurveId) bean).getCurveName();
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
   * The bean-builder for {@code SimpleCurveId}.
   */
  private static final class Builder extends DirectFieldsBeanBuilder<SimpleCurveId> {

    private CurveGroupName curveGroupName;
    private CurveName curveName;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case -382645893:  // curveGroupName
          return curveGroupName;
        case 771153946:  // curveName
          return curveName;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case -382645893:  // curveGroupName
          this.curveGroupName = (CurveGroupName) newValue;
          break;
        case 771153946:  // curveName
          this.curveName = (CurveName) newValue;
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
    public SimpleCurveId build() {
      return new SimpleCurveId(
          curveGroupName,
          curveName);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(96);
      buf.append("SimpleCurveId.Builder{");
      buf.append("curveGroupName").append('=').append(JodaBeanUtils.toString(curveGroupName)).append(',').append(' ');
      buf.append("curveName").append('=').append(JodaBeanUtils.toString(curveName));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
