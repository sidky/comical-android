package com.github.sidky.comical

import android.content.pm.PackageManager
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(JUnit4::class)
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val m = Mockito.mock(PackageManager::class.java)
        assertNotNull(m)
        assertEquals(4, 2 + 2)
    }
}