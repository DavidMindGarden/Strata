/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.engine.config;

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

/**
 * Configuration for a set of tasks that calculate values of measures for a set of targets.
 * <p>
 * There is one task for each value that will be calculated. Therefore the number of tasks is
 * the number of targets multiplied by the number of columns.
 * <p>
 * The measures are included for reference.
 */
@BeanDefinition
public final class CalculationTasksConfig implements ImmutableBean {

  /** Configuration for each of tasks that perform the individual calculations. */
  @PropertyDefinition(validate = "notNull")
  private final ImmutableList<CalculationTaskConfig> taskConfigurations;

  /** The columns that define the calculations. */
  @PropertyDefinition(validate = "notNull")
  private final ImmutableList<Column> columns;

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code CalculationTasksConfig}.
   * @return the meta-bean, not null
   */
  public static CalculationTasksConfig.Meta meta() {
    return CalculationTasksConfig.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(CalculationTasksConfig.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static CalculationTasksConfig.Builder builder() {
    return new CalculationTasksConfig.Builder();
  }

  private CalculationTasksConfig(
      List<CalculationTaskConfig> taskConfigurations,
      List<Column> columns) {
    JodaBeanUtils.notNull(taskConfigurations, "taskConfigurations");
    JodaBeanUtils.notNull(columns, "columns");
    this.taskConfigurations = ImmutableList.copyOf(taskConfigurations);
    this.columns = ImmutableList.copyOf(columns);
  }

  @Override
  public CalculationTasksConfig.Meta metaBean() {
    return CalculationTasksConfig.Meta.INSTANCE;
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
   * Gets configuration for each of tasks that perform the individual calculations.
   * @return the value of the property, not null
   */
  public ImmutableList<CalculationTaskConfig> getTaskConfigurations() {
    return taskConfigurations;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the columns that define the calculations.
   * @return the value of the property, not null
   */
  public ImmutableList<Column> getColumns() {
    return columns;
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
      CalculationTasksConfig other = (CalculationTasksConfig) obj;
      return JodaBeanUtils.equal(getTaskConfigurations(), other.getTaskConfigurations()) &&
          JodaBeanUtils.equal(getColumns(), other.getColumns());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getTaskConfigurations());
    hash = hash * 31 + JodaBeanUtils.hashCode(getColumns());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("CalculationTasksConfig{");
    buf.append("taskConfigurations").append('=').append(getTaskConfigurations()).append(',').append(' ');
    buf.append("columns").append('=').append(JodaBeanUtils.toString(getColumns()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code CalculationTasksConfig}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code taskConfigurations} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ImmutableList<CalculationTaskConfig>> taskConfigurations = DirectMetaProperty.ofImmutable(
        this, "taskConfigurations", CalculationTasksConfig.class, (Class) ImmutableList.class);
    /**
     * The meta-property for the {@code columns} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ImmutableList<Column>> columns = DirectMetaProperty.ofImmutable(
        this, "columns", CalculationTasksConfig.class, (Class) ImmutableList.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "taskConfigurations",
        "columns");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1026924322:  // taskConfigurations
          return taskConfigurations;
        case 949721053:  // columns
          return columns;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public CalculationTasksConfig.Builder builder() {
      return new CalculationTasksConfig.Builder();
    }

    @Override
    public Class<? extends CalculationTasksConfig> beanType() {
      return CalculationTasksConfig.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code taskConfigurations} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ImmutableList<CalculationTaskConfig>> taskConfigurations() {
      return taskConfigurations;
    }

    /**
     * The meta-property for the {@code columns} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ImmutableList<Column>> columns() {
      return columns;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 1026924322:  // taskConfigurations
          return ((CalculationTasksConfig) bean).getTaskConfigurations();
        case 949721053:  // columns
          return ((CalculationTasksConfig) bean).getColumns();
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
   * The bean-builder for {@code CalculationTasksConfig}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<CalculationTasksConfig> {

    private List<CalculationTaskConfig> taskConfigurations = ImmutableList.of();
    private List<Column> columns = ImmutableList.of();

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(CalculationTasksConfig beanToCopy) {
      this.taskConfigurations = beanToCopy.getTaskConfigurations();
      this.columns = beanToCopy.getColumns();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1026924322:  // taskConfigurations
          return taskConfigurations;
        case 949721053:  // columns
          return columns;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 1026924322:  // taskConfigurations
          this.taskConfigurations = (List<CalculationTaskConfig>) newValue;
          break;
        case 949721053:  // columns
          this.columns = (List<Column>) newValue;
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
    public CalculationTasksConfig build() {
      return new CalculationTasksConfig(
          taskConfigurations,
          columns);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code taskConfigurations} property in the builder.
     * @param taskConfigurations  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder taskConfigurations(List<CalculationTaskConfig> taskConfigurations) {
      JodaBeanUtils.notNull(taskConfigurations, "taskConfigurations");
      this.taskConfigurations = taskConfigurations;
      return this;
    }

    /**
     * Sets the {@code taskConfigurations} property in the builder
     * from an array of objects.
     * @param taskConfigurations  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder taskConfigurations(CalculationTaskConfig... taskConfigurations) {
      return taskConfigurations(ImmutableList.copyOf(taskConfigurations));
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

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(96);
      buf.append("CalculationTasksConfig.Builder{");
      buf.append("taskConfigurations").append('=').append(JodaBeanUtils.toString(taskConfigurations)).append(',').append(' ');
      buf.append("columns").append('=').append(JodaBeanUtils.toString(columns));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}