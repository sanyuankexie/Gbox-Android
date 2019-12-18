package com.guet.flexbox.databinding

import android.content.Context
import com.guet.flexbox.el.PropsELContext

internal class BoolAttribute(scope: Map<String, Boolean>, fallback: Boolean?) : AttributeInfo<Boolean>(scope, fallback) {
    override fun cast(c: Context, props: PropsELContext, raw: String): Boolean? {
        return if (raw.isExpr) {
            props.tryGetValue(raw, fallback)
        } else {
            try {
                raw.toBoolean()
            } catch (e: Exception) {
                fallback
            }
        }
    }
}