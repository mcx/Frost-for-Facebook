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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pitchedapps.frost.components.UseCases
import com.pitchedapps.frost.compose.FrostWeb
import com.pitchedapps.frost.extension.FrostCoreExtension
import mozilla.components.browser.state.helper.Target
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.concept.engine.Engine

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
  val vm: MainScreenViewModel = viewModel()

  if (vm.contextId.isEmpty()) return // Not ready

  DisposableEffect(vm.store) {
    val feature =
      FrostCoreExtension(
        runtime = vm.engine,
        store = vm.store,
        converter = vm.extensionModelConverter,
      )

    feature.start()

    onDispose { feature.stop() }
  }

  MainContainer(
    modifier = modifier,
    engine = vm.engine,
    store = vm.store,
    contextId = vm.contextId,
    useCases = vm.useCases,
    tabIndex = vm.tabIndex,
    tabs = vm.tabs,
    onTabSelect = { selectedIndex: Int ->
      if (selectedIndex == vm.tabIndex) {
        vm.useCases.homeTabs.reloadTab(selectedIndex)
        //          context.launchFloatingUrl(FACEBOOK_M_URL)
      } else {
        // Change? What if previous selected tab is not home tab
        vm.useCases.homeTabs.selectHomeTab(selectedIndex)
        vm.tabIndex = selectedIndex
      }
    },
  )
}

@Composable
private fun MainContainer(
  engine: Engine,
  store: BrowserStore,
  contextId: String,
  useCases: UseCases,
  tabIndex: Int,
  tabs: List<MainTabItem>,
  onTabSelect: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  LaunchedEffect(contextId) {
    useCases.homeTabs.createHomeTabs(contextId, tabIndex, tabs.map { it.url })
  }

  Column(modifier = modifier) {
    MainTabRow(selectedIndex = tabIndex, items = tabs, onTabSelect = onTabSelect)
    // For tab switching, must use SelectedTab
    // https://github.com/mozilla-mobile/android-components/issues/12798
    FrostWeb(engine = engine, store = store, target = Target.SelectedTab)
  }
}

@Composable
fun MainTabRow(
  selectedIndex: Int,
  items: List<MainTabItem>,
  onTabSelect: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  TabRow(modifier = modifier, selectedTabIndex = selectedIndex, indicator = {}) {
    items.forEachIndexed { i, item ->
      val selected = selectedIndex == i
      Tab(selected = selected, onClick = { onTabSelect(i) }) {
        MainTabItem(modifier = Modifier.padding(16.dp), item = item, selected = selected)
      }
    }
  }
}

@Composable
private fun MainTabItem(item: MainTabItem, selected: Boolean, modifier: Modifier = Modifier) {
  val alpha by
    animateFloatAsState(
      targetValue = if (selected) 1f else ContentAlpha.medium,
      label = "Tab Alpha",
    )
  Icon(
    modifier = modifier.alpha(alpha),
    contentDescription = item.title,
    imageVector = item.icon,
  )
}
