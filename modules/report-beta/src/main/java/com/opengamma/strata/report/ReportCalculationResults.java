/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.strata.report;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

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
import com.opengamma.strata.engine.Column;
import com.opengamma.strata.engine.calculations.Results;

/**
 * Stores a set of engine calculation results along with the context required to run reports.
 */
@BeanDefinition
public class ReportCalculationResults implements ImmutableBean {

  /** The valuation date. */
  @PropertyDefinition(validate = "notNull")
  private final LocalDate valuationDate;
  
  /** The columns contained in the results. */
  @PropertyDefinition(validate = "notNull")
  private final List<Column> columns;
  
  /** The calculation results. */
  @PropertyDefinition(validate = "notNull")
  private final Results calculationResults;
  
  public static ReportCalculationResults of(
      LocalDate valuationDate,
      List<Column> columns,
      Results calculationResults) {
    
    return ReportCalculationResults.builder()
        .valuationDate(valuationDate)
        .columns(columns)
        .calculationResults(calculationResults)
        .build();
  }
 
  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code ReportCalculationResults}.
   * @return the meta-bean, not null
   */
  public static ReportCalculationResults.Meta meta() {
    return ReportCalculationResults.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(ReportCalculationResults.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static ReportCalculationResults.Builder builder() {
    return new ReportCalculationResults.Builder();
  }

  /**
   * Restricted constructor.
   * @param builder  the builder to copy from, not null
   */
  protected ReportCalculationResults(ReportCalculationResults.Builder builder) {
    JodaBeanUtils.notNull(builder.valuationDate, "valuationDate");
    JodaBeanUtils.notNull(builder.columns, "columns");
    JodaBeanUtils.notNull(builder.calculationResults, "calculationResults");
    this.valuationDate = builder.valuationDate;
    this.columns = ImmutableList.copyOf(builder.columns);
    this.calculationResults = builder.calculationResults;
  }

  @Override
  public ReportCalculationResults.Meta metaBean() {
    return ReportCalculationResults.Meta.INSTANCE;
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
   * Gets the valuation date.
   * @return the value of the property, not null
   */
  public LocalDate getValuationDate() {
    return valuationDate;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the columns contained in the results.
   * @return the value of the property, not null
   */
  public List<Column> getColumns() {
    return columns;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the calculation results.
   * @return the value of the property, not null
   */
  public Results getCalculationResults() {
    return calculationResults;
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
      ReportCalculationResults other = (ReportCalculationResults) obj;
      return JodaBeanUtils.equal(getValuationDate(), other.getValuationDate()) &&
          JodaBeanUtils.equal(getColumns(), other.getColumns()) &&
          JodaBeanUtils.equal(getCalculationResults(), other.getCalculationResults());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getValuationDate());
    hash = hash * 31 + JodaBeanUtils.hashCode(getColumns());
    hash = hash * 31 + JodaBeanUtils.hashCode(getCalculationResults());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(128);
    buf.append("ReportCalculationResults{");
    int len = buf.length();
    toString(buf);
    if (buf.length() > len) {
      buf.setLength(buf.length() - 2);
    }
    buf.append('}');
    return buf.toString();
  }

  protected void toString(StringBuilder buf) {
    buf.append("valuationDate").append('=').append(JodaBeanUtils.toString(getValuationDate())).append(',').append(' ');
    buf.append("columns").append('=').append(JodaBeanUtils.toString(getColumns())).append(',').append(' ');
    buf.append("calculationResults").append('=').append(JodaBeanUtils.toString(getCalculationResults())).append(',').append(' ');
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code ReportCalculationResults}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code valuationDate} property.
     */
    private final MetaProperty<LocalDate> valuationDate = DirectMetaProperty.ofImmutable(
        this, "valuationDate", ReportCalculationResults.class, LocalDate.class);
    /**
     * The meta-property for the {@code columns} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<Column>> columns = DirectMetaProperty.ofImmutable(
        this, "columns", ReportCalculationResults.class, (Class) List.class);
    /**
     * The meta-property for the {@code calculationResults} property.
     */
    private final MetaProperty<Results> calculationResults = DirectMetaProperty.ofImmutable(
        this, "calculationResults", ReportCalculationResults.class, Results.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "valuationDate",
        "columns",
        "calculationResults");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 113107279:  // valuationDate
          return valuationDate;
        case 949721053:  // columns
          return columns;
        case 2096132333:  // calculationResults
          return calculationResults;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public ReportCalculationResults.Builder builder() {
      return new ReportCalculationResults.Builder();
    }

    @Override
    public Class<? extends ReportCalculationResults> beanType() {
      return ReportCalculationResults.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code valuationDate} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<LocalDate> valuationDate() {
      return valuationDate;
    }

    /**
     * The meta-property for the {@code columns} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<List<Column>> columns() {
      return columns;
    }

    /**
     * The meta-property for the {@code calculationResults} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Results> calculationResults() {
      return calculationResults;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 113107279:  // valuationDate
          return ((ReportCalculationResults) bean).getValuationDate();
        case 949721053:  // columns
          return ((ReportCalculationResults) bean).getColumns();
        case 2096132333:  // calculationResults
          return ((ReportCalculationResults) bean).getCalculationResults();
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
   * The bean-builder for {@code ReportCalculationResults}.
   */
  public static class Builder extends DirectFieldsBeanBuilder<ReportCalculationResults> {

    private LocalDate valuationDate;
    private List<Column> columns = ImmutableList.of();
    private Results calculationResults;

    /**
     * Restricted constructor.
     */
    protected Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    protected Builder(ReportCalculationResults beanToCopy) {
      this.valuationDate = beanToCopy.getValuationDate();
      this.columns = ImmutableList.copyOf(beanToCopy.getColumns());
      this.calculationResults = beanToCopy.getCalculationResults();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 113107279:  // valuationDate
          return valuationDate;
        case 949721053:  // columns
          return columns;
        case 2096132333:  // calculationResults
          return calculationResults;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 113107279:  // valuationDate
          this.valuationDate = (LocalDate) newValue;
          break;
        case 949721053:  // columns
          this.columns = (List<Column>) newValue;
          break;
        case 2096132333:  // calculationResults
          this.calculationResults = (Results) newValue;
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
    public ReportCalculationResults build() {
      return new ReportCalculationResults(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code valuationDate} property in the builder.
     * @param valuationDate  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder valuationDate(LocalDate valuationDate) {
      JodaBeanUtils.notNull(valuationDate, "valuationDate");
      this.valuationDate = valuationDate;
      return this;
    }

    /**
     * Sets the {@code columns} property in the builder.
     * @param columns  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder columns(List<Column> columns) {
      JodaBeanUtils.notNull(columns, "columns");
      this.columns = columns;
      return this;
    }

    /**
     * Sets the {@code columns} property in the builder
     * from an array of objects.
     * @param columns  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder columns(Column... columns) {
      return columns(ImmutableList.copyOf(columns));
    }

    /**
     * Sets the {@code calculationResults} property in the builder.
     * @param calculationResults  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder calculationResults(Results calculationResults) {
      JodaBeanUtils.notNull(calculationResults, "calculationResults");
      this.calculationResults = calculationResults;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(128);
      buf.append("ReportCalculationResults.Builder{");
      int len = buf.length();
      toString(buf);
      if (buf.length() > len) {
        buf.setLength(buf.length() - 2);
      }
      buf.append('}');
      return buf.toString();
    }

    protected void toString(StringBuilder buf) {
      buf.append("valuationDate").append('=').append(JodaBeanUtils.toString(valuationDate)).append(',').append(' ');
      buf.append("columns").append('=').append(JodaBeanUtils.toString(columns)).append(',').append(' ');
      buf.append("calculationResults").append('=').append(JodaBeanUtils.toString(calculationResults)).append(',').append(' ');
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
  }
