package com.guet.flexbox.playground

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.gson.Gson
import com.guet.flexbox.compiler.Compiler
import com.guet.flexbox.data.LockedInfo
import com.guet.flexbox.data.NodeInfo
import com.guet.flexbox.databinding.DataBindingUtils
import java.util.Collections.singletonMap

class AssetDisplay(
        val banner: List<LockedInfo>,
        val function: LockedInfo,
        val feed: List<LockedInfo>
) {
    companion object Default {
        @JvmStatic
        @WorkerThread
        fun loadDefault(c: Context): AssetDisplay {
            val res = c.resources
            val gson = Gson()
            val assets = res.assets
            val banner = res.getStringArray(R.array.banner_paths).map {
                val input = assets.open(it)
                val lockedInfo = DataBindingUtils.bind(c, gson.fromJson(
                        Compiler.compile(input),
                        NodeInfo::class.java
                ), singletonMap("url", it))
                input.close()
                lockedInfo
            }
            val feed = res.getStringArray(R.array.feed_paths).map {
                val input = assets.open(it)
                val lockedInfo = DataBindingUtils.bind(c, gson.fromJson(
                        Compiler.compile(input),
                        NodeInfo::class.java
                ), singletonMap("url", it))
                input.close()
                lockedInfo
            }
            val functionPath = res.getString(R.string.function_path)
            val input = assets.open(functionPath)
            val function = DataBindingUtils.bind(c, gson.fromJson(
                    Compiler.compile(input),
                    NodeInfo::class.java
            ), singletonMap("url", functionPath))
            input.close()
            return AssetDisplay(banner, function, feed)
        }
    }
}