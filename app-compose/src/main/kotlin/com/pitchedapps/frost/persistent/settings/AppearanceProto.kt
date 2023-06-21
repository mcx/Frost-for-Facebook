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
package com.pitchedapps.frost.persistent.settings

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import com.google.protobuf.InvalidProtocolBufferException
import com.pitchedapps.frost.proto.settings.Appearance
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Singleton

private object AppearanceProtoSerializer : Serializer<Appearance> {
  override val defaultValue: Appearance = Appearance.getDefaultInstance()

  override suspend fun readFrom(input: InputStream): Appearance {
    try {
      return Appearance.parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
      throw CorruptionException("Cannot read proto.", exception)
    }
  }

  override suspend fun writeTo(t: Appearance, output: OutputStream) = t.writeTo(output)
}

@Module
@InstallIn(SingletonComponent::class)
object AppearanceProtoModule {
  @Provides
  @Singleton
  fun provideDataStore(@ApplicationContext context: Context): DataStore<Appearance> =
    DataStoreFactory.create(
      AppearanceProtoSerializer,
    ) {
      context.dataStoreFile("settings/appearance.pb")
    }
}
