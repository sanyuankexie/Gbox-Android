package com.guet.flexbox.playground

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.didichuxing.doraemonkit.util.UIUtils
import com.facebook.litho.LithoView
import com.facebook.litho.Row
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaJustify
import com.google.gson.Gson
import com.guet.flexbox.DynamicBox
import com.guet.flexbox.EventListener
import com.guet.flexbox.compiler.Compiler
import com.guet.flexbox.data.NodeInfo
import com.guet.flexbox.databinding.DataBindingUtils
import java.util.*
import kotlin.collections.HashSet

class SearchActivity : AppCompatActivity(), EventListener {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var list: LithoView
    private lateinit var editText: EditText
    private var popupWindow: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.apply {
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        }
        setContentView(R.layout.activity_search)
        sharedPreferences = getSharedPreferences("history", Context.MODE_PRIVATE)
        list = findViewById(R.id.list)
        AnimatorSet().apply {
            duration = 250L
            interpolator = DecelerateInterpolator()
            playTogether(ValueAnimator.ofFloat(
                    UIUtils.dp2px(
                            this@SearchActivity,
                            -100f
                    ).toFloat(), 0f)
                    .apply {
                        addUpdateListener {
                            val value = it.animatedValue as Float
                            list.translationX = value
                        }
                    },
                    ValueAnimator.ofFloat(0f, 1f).apply {
                        addUpdateListener {
                            val value = it.animatedValue as Float
                            list.alpha = value
                        }
                    })
        }.start()
        editText = findViewById(R.id.search)
        editText.apply {
            setOnFocusChangeListener { v, hasFocus ->
                popupWindow?.dismiss()
                popupWindow = null
                if (hasFocus) {
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE)
                            as ClipboardManager
                    val data = clipboard.primaryClip
                    if (data != null && data.itemCount > 0) {
                        val item = data.getItemAt(0).text
                        if (TextUtils.equals(item, (v as TextView).text)) {
                            return@setOnFocusChangeListener
                        }
                        val window = PopupWindow()
                        val content = layoutInflater.inflate(
                                R.layout.text_popup_window,
                                FrameLayout(this@SearchActivity),
                                false
                        )
                        val text = content.findViewById<TextView>(R.id.text)
                        window.contentView = content
                        window.width = UIUtils.dp2px(this@SearchActivity, 300f)
                        window.height = ViewGroup.LayoutParams.MATCH_PARENT
                        text.text = item
                        content.setOnClickListener {
                            popupWindow?.dismiss()
                            popupWindow = null
                            handleEvent(item.toString(), emptyArray())
                        }
                        window.showAsDropDown(v, 0, v.height)
                        popupWindow = window
                    }
                }
            }
            addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {
                    popupWindow?.dismiss()
                    popupWindow = null
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    val text = v as TextView
                    handleEvent(text.text.toString(), emptyArray())
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }
        val cancel = findViewById<View>(R.id.cancel)
        cancel.setOnClickListener { finishAfterTransition() }
        loadHistory()
    }

    private fun loadHistory() {
        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            val gson = Gson()
            val input = resources.assets.open("layout/search/history_list.xml")
            val s = Compiler.compile(input)
            val contentRaw = gson.fromJson(s, NodeInfo::class.java)
            val data = Collections.singletonMap(
                    "list",
                    sharedPreferences.getStringSet(
                            "history_list",
                            emptySet()
                    )!!.toList()
            )
            val content = DataBindingUtils.bind(this, contentRaw, data)
            runOnUiThread {
                val c = list.componentContext
                list.setComponentAsync(Row.create(c)
                        .alignItems(YogaAlign.CENTER)
                        .justifyContent(YogaJustify.CENTER)
                        .child(DynamicBox.create(c)
                                .content(content)
                        ).build())
            }
        }
    }

    override fun handleEvent(key: String, value: Array<out Any>) {
        if (key.isEmpty()) {
            return
        }
        val set = HashSet<String>(sharedPreferences.getStringSet(
                "history_list",
                emptySet()
        )!!)
        set.add(key)
        sharedPreferences.edit()
                .putStringSet("list", set)
                .apply()
        startActivity(Intent(this, CodeActivity::class.java)
                .apply {
                    putExtra("url", key)
                }
        )
        finish()
    }
}
