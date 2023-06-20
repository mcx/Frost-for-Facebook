/*
 * Copyright 2023 Allan Wang
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.pitchedapps.frost.web

import mozilla.components.lib.state.Action

/**
 * See
 * https://github.com/mozilla-mobile/firefox-android/blob/main/android-components/components/browser/state/src/main/java/mozilla/components/browser/state/action/BrowserAction.kt
 *
 * For firefox example
 */
sealed interface FrostAction : Action

/**
 * [FrostAction] dispatched to indicate that the store is initialized and ready to use. This action
 * is dispatched automatically before any other action is processed. Its main purpose is to trigger
 * initialization logic in middlewares. The action itself has no effect on the [FrostWebState].
 */
object InitAction : FrostAction

/** Action indicating current url state. */
data class UpdateUrlAction(val url: String) : FrostAction

/** Action indicating current title state. */
data class UpdateTitleAction(val title: String?) : FrostAction

data class UpdateNavigationAction(val canGoBack: Boolean, val canGoForward: Boolean) : FrostAction

data class UpdateProgressAction(val progress: Int) : FrostAction

/** Action triggered by user, leading to transient state changes. */
sealed interface UserAction : FrostAction {

  /** Action to load new url. */
  data class LoadUrlAction(val url: String) : UserAction

  object GoBackAction : UserAction

  object GoForwardAction : UserAction
}

/** Response triggered by webview, indicating [UserAction] fulfillment. */
sealed interface ResponseAction : FrostAction {

  data class LoadUrlResponseAction(val url: String) : ResponseAction

  data class WebStepResponseAction(val steps: Int) : ResponseAction
}
