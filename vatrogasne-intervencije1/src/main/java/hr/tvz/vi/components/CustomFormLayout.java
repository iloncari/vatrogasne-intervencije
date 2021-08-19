/*
 * CustomFormLayout CustomFormLayout.java.
 *
 */
package hr.tvz.vi.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Binder.BindingBuilder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.converter.Converter;

import hr.tvz.vi.util.Constants.StyleConstants;
import hr.tvz.vi.util.Utils;

import org.apache.commons.lang3.StringUtils;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.html.VH4;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class CustomFormLayout.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @param <T> the generic type
 * @since 8:20:09 PM Aug 11, 2021
 */
@Slf4j
public class CustomFormLayout<T> extends VVerticalLayout {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -9071058555999755515L;

  /** The form title. */
  private final VH4 formTitle = new VH4().withVisible(false);

  /** The binder. */
  private final Binder<T> binder;

  private final T bean;

  /**
   * Instantiates a new custom form layout.
   */
  public CustomFormLayout(final Binder<T> topSectionBinder, T bean) {
    this.binder = topSectionBinder;
    this.bean = bean;
    setWidthFull();
    add(formTitle);
  }

  /**
   * Adds the form item.
   *
   * @param component the component
   * @param translationKey the translation key
   * @param required the required
   */
  public void addFormItem(final Component component, final String translationKey, Object... params) {
    setLabel(component, translationKey, params);
    component.getElement().setAttribute("class", StyleConstants.WIDTH_100.getName());
    add(component);
  }

  /**
   * Adds the read only item.
   *
   * @param translationKey the translation key
   * @param text the text
   */
  public Paragraph addReadOnlyItem(final String labelTranslationKey, final Component valueComponent) {
    final StringBuilder builder = new StringBuilder();
    if (StringUtils.isNotBlank(labelTranslationKey)) {
      builder.append(getTranslation(labelTranslationKey)).append(": ");
    }
    // Utils.removeAllThemes(valueComponent);
    final Paragraph paragraph = new Paragraph(new Span(builder.toString()), valueComponent);
    // paragraph.addClassName(StyleConstants.FORM_INFO.getName());
    add(paragraph);
    return paragraph;
  }

  /**
   * Adds the read only item.
   *
   * @param translationKey the translation key
   * @param text the text
   */
  public Paragraph addReadOnlyItem(final String labelTranslationKey, final String text, final String... styleClasses) {
    final StringBuilder builder = new StringBuilder();
    if (StringUtils.isNotBlank(labelTranslationKey)) {
      builder.append(getTranslation(labelTranslationKey)).append(": ");
    }
    final Span value = new Span(text);
    value.addClassNames(styleClasses);
    final Paragraph paragraph = new Paragraph(new Span(builder.toString()), value);
    // paragraph.addClassName(StyleConstants.FORM_INFO.getName());
    add(paragraph);
    return paragraph;
  }

  /**
   * Adds the save bean button.
   *
   * @return the v button
   */
  public VButton addSaveBeanButton(ComponentEventListener<ClickEvent<Button>> saveListener) {
    final VButton saveButton = new VButton(getTranslation("button.save")).withClickListener(saveListener);
    add(saveButton);
    return saveButton;
  }

  /**
   * Adds the two column items layout.
   *
   * @param left the left
   * @param right the right
   * @return the component
   */
  public Component addTwoColumnItemsLayout(Component left, Component right) {
    final VHorizontalLayout layout = new VHorizontalLayout(left).withClassName(StyleConstants.WIDTH_100.getName());
    if (null != right) {
      layout.add(right);
      right.getElement().setAttribute("class", StyleConstants.WIDTH_50.getName());
    }
    left.getElement().setAttribute("class", StyleConstants.WIDTH_50.getName());
    add(layout);
    return layout;
  }

  /**
   * Adds the two column items layout.
   *
   * @param left the left
   * @param leftStyle the left style
   * @param right the right
   * @param rightStyle the right style
   * @return the component
   */
  public Component addTwoColumnItemsLayout(Component left, StyleConstants leftStyle, Component right, StyleConstants rightStyle) {
    final VHorizontalLayout layout = new VHorizontalLayout(left).withClassName(StyleConstants.WIDTH_100.getName());
    if (null != right) {
      layout.add(right);
      if (rightStyle != null) {
        right.getElement().setAttribute("class", rightStyle.getName());
      }
    }
    left.getElement().setAttribute("class", leftStyle.getName());
    add(layout);
    return layout;
  }

  /**
   * Process binder.
   *
   * @param <R> the generic type
   * @param <S> the generic type
   * @param field the field
   * @param converter the converter
   * @param validator the validator
   * @param required the required
   * @param propertyPath the property path
   */
  public <R, S> void processBinder(final HasValue<?, R> field, final Converter<R, S> converter, final Validator<? super R> validator, final boolean required,
    final String propertyPath) {
    final BindingBuilder<T, R> bindingBuilder = binder.forField(field);
    if (required) {
      bindingBuilder.asRequired(getTranslation("form.field.required"));
    }
    if (null != validator) {
      bindingBuilder.withValidator(j -> {
        if (j == null) {
          return true;
        }
        return false;
      }, "");
      bindingBuilder.withValidator(validator);
    }
    if (null != converter) {
      bindingBuilder.withConverter(converter);
    }
    bindingBuilder.bind(propertyPath);
  }

  /**
   * Read bead.
   */
  public void readBean() {
    binder.readBean(bean);
  }

  /**
   * Sets the form title.
   *
   * @param titleKey the title key
   * @param labelParams the label params
   */
  public void setFormTitle(String titleKey, Object... labelParams) {
    getTranslation(titleKey, labelParams);
    formTitle.setText(getTranslation(titleKey, labelParams));
    formTitle.setVisible(true);
  }

  /**
   * Sets the label.
   *
   * @param component the component
   * @param taskFormModel the task form model
   */
  public void setLabel(final Component component, final String translationKey, Object... labelParams) {
    final String label = getTranslation(translationKey, labelParams);
    component.getElement().setAttribute("label", label);
  }

  /**
   * Write bean.
   *
   * @return true, if successful
   */
  public boolean writeBean() {
    try {
      binder.writeBean(bean);
      return true;
    } catch (final ValidationException e) {
      Utils.showErrorNotification(3000, Position.TOP_CENTER, "beanNESTO NE VALJA");
      return false;
    }
  }

}
