package de.fgoetze.mcugraph

import javafx.beans.binding.Bindings
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectPropertyBase
import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.beans.value.ObservableValueBase

class BidirectionalPropertyBinding<T>(
    val prop: Property<T>
) {

    private var otherProp: Property<T>? = null

    private var lazyIsBoundProp: SimpleBooleanProperty? = null

    val isBoundProperty: BooleanProperty
        get() {
            var lazy = lazyIsBoundProp
            if (lazy == null) {
                lazy = SimpleBooleanProperty(otherProp != null)
                lazyIsBoundProp = lazy
            }
            return lazy
        }

    fun bind(other: Property<T>?) {
        otherProp?.unbindBidirectional(prop)
        otherProp = null
        if (other != null) prop.bindBidirectional(other)
        otherProp = other
        lazyIsBoundProp?.value = other != null
    }

    fun unbind() {
        otherProp?.unbindBidirectional(prop)
        lazyIsBoundProp?.value = false
    }
}

fun <T, S> ObservableValue<T>.nested(mapping: (T) -> ObservableValue<S>?): ObservableValue<S?> {
    return object : ObservableValueBase<S?>() {

        private var curInnerObs: ObservableValue<S>? = null

        private val outerListener = ChangeListener<T> { _, old, new ->
            if (old === new) return@ChangeListener
            curInnerObs?.removeListener(innerListener)
            curInnerObs = mapping(new)
            curInnerObs?.addListener(innerListener)
            fireValueChangedEvent()
        }

        private val innerListener = ChangeListener<S> { _, _, _ ->
            fireValueChangedEvent()
        }

        init {
            this@nested.addListener(outerListener)
            curInnerObs = mapping(this@nested.value)
            curInnerObs?.addListener(innerListener)
        }

        override fun getValue(): S? = curInnerObs?.value
    }
}

fun <T : Any> ObservableValue<T?>.withDefault(defaultValue: T): Property<T> {
    return object : ObjectPropertyBase<T>() {

        private val innerProp: Property<T>? = this@withDefault as? Property<T>

        override fun getBean() = innerProp?.bean
        override fun getName() = innerProp?.name

        init {
            this@withDefault.addListener { _, _, _ -> fireValueChangedEvent() }
        }

        override fun get(): T = this@withDefault.value ?: defaultValue

        override fun set(newValue: T) {
            innerProp?.value = newValue
            fireValueChangedEvent()
        }
    }
}

fun ObservableValue<Boolean>.not(): ObservableValue<Boolean> = Bindings.createBooleanBinding({
    this@not.value != true
}, this@not)

fun <T, S> ObservableValue<T>.mapValue(mapping: (T) -> S): ObservableValue<S> = Bindings.createObjectBinding({
    mapping(this@mapValue.value)
}, this@mapValue)

fun <T, S> Property<T>.convertBidirectional(
    fromThisToOther: (T) -> S,
    fromOtherToThis: (S) -> T
): Property<S> {
    return object : ObjectPropertyBase<S>() {

        init {
            this@convertBidirectional.addListener { _, _, _ -> fireValueChangedEvent() }
        }

        override fun getBean() = this@convertBidirectional.bean

        override fun getName() = this@convertBidirectional.name

        override fun get(): S {
            return fromThisToOther(this@convertBidirectional.value)
        }

        override fun set(newValue: S) {
            this@convertBidirectional.value = fromOtherToThis(newValue)
        }
    }
}