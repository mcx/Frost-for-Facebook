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
package com.pitchedapps.frost.main

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import com.pitchedapps.frost.components.UseCases
import com.pitchedapps.frost.extension.ExtensionModelConverter
import com.pitchedapps.frost.facebook.FbItem
import com.pitchedapps.frost.hilt.FrostComponents
import com.pitchedapps.frost.proto.Account
import com.pitchedapps.frost.proto.settings.Appearance
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.concept.engine.Engine

@HiltViewModel
class MainScreenViewModel
@Inject
internal constructor(
  @ApplicationContext context: Context,
  val components: FrostComponents,
  val engine: Engine,
  val store: BrowserStore,
  val useCases: UseCases,
  val extensionModelConverter: ExtensionModelConverter,
  accountDataStore: DataStore<Account>,
  appearanceDataStore: DataStore<Appearance>,
) : ViewModel() {

  val tabsFlow: Flow<List<MainTabItem>> =
    appearanceDataStore.data.map { appearance ->
      appearance.mainTabsList.mapNotNull { FbItem.fromKey(it)?.tab(context) }
    }

  val contextIdFlow: Flow<String> = accountDataStore.data.map { it.accountId }

  var tabIndex: Int by mutableStateOf(0)
}

private fun FbItem.tab(context: Context) =
  MainTabItem(
    title = context.getString(titleId),
    icon = icon,
    url = url,
  )
