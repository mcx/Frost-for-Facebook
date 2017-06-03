package com.pitchedapps.frost

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerUIUtils
import com.pitchedapps.frost.facebook.FbCookie
import com.pitchedapps.frost.utils.CrashReportingTree
import com.pitchedapps.frost.utils.Prefs
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import timber.log.Timber
import timber.log.Timber.DebugTree

/**
 * Created by Allan Wang on 2017-05-28.
 */
class FrostApp : Application() {

    override fun onCreate() {
        if (BuildConfig.DEBUG) Timber.plant(DebugTree())
        else Timber.plant(CrashReportingTree())
        FlowManager.init(FlowConfig.Builder(this).build())
        Prefs(this)
        FbCookie()
        super.onCreate()

        //Drawer profile loading logic
        DrawerImageLoader.init(object : AbstractDrawerImageLoader() {
            override fun set(imageView: ImageView, uri: Uri, placeholder: Drawable, tag: String) {
                Glide.with(imageView.context).load(uri).apply(RequestOptions().placeholder(placeholder)).into(imageView)
            }

            override fun placeholder(ctx: Context, tag: String): Drawable {
                when (tag) {
                    DrawerImageLoader.Tags.PROFILE.name, DrawerImageLoader.Tags.ACCOUNT_HEADER.name -> DrawerUIUtils.getPlaceHolder(ctx)
                }
                return super.placeholder(ctx, tag);
            }
        })
    }


}