package pt.isel

import org.junit.jupiter.api.AfterEach
import java.io.File
import java.io.FileWriter
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val imgUrl = "https://dev.java/assets/images/duke/duke_star7.png"
private val imgFile = File(imgUrl.substringAfterLast("/"))

class TempImageTest {
    @AfterEach
    fun tearDown() {
        File("temp.txt").delete()
    }

    @Test
    fun `show how to call close`() {
        var writer: FileWriter? = null
        try {
            writer = FileWriter("temp.txt")
            writer.write("...")
        } finally {
            writer?.close()
        }

        File("temp.txt").delete()
        FileWriter("temp.txt").use { writer2 ->
            writer2.write("...")
        }
    }

    @Test
    fun `check same image downloaded once and deleted on close`() {
        TempImage(imgUrl)
            .use {
                assertTrue(it.downloaded)
                TempImage(imgUrl)
                    .use { second ->
                        assertFalse(second.downloaded)
                    }
            }
        assertFalse(imgFile.exists())
    }

    @Test
    fun `check deleted file via finalize`() {
        fun loadImageAndForgetClose() {
            TempImage(imgUrl)
                .also { // Not calling close !!!
                    assertTrue(it.downloaded)
                }
        }
        loadImageAndForgetClose()
        assertTrue(imgFile.exists())
        /**
         * After an Object is considered candidate for garbage collection the finalization may run.
         * Yet, finalization run on a different thread, and we may have to sleep a bit to see changes.
         */
        System.gc()
        Thread.sleep(100)
        assertFalse(imgFile.exists())
    }

    @Test
    fun `trying delete twice via close and finalize`() {
        fun loadImageAndForgetClose() {
            TempImage(imgUrl)
                .use { // use ensure close is called
                    assertTrue(it.downloaded)
                }
        }
        loadImageAndForgetClose()
        assertFalse(imgFile.exists()) // Already deleted by call to close from the use block.
        /**
         * Nevertheless finalization may run and call close() for the second time.
         */
        System.gc()
        Thread.sleep(100)
    }

    @Test
    fun `check deleted file via cleaner`() {
        fun loadImageAndForgetClose() {
            TempImageCleanable(imgUrl)
                .also { // NOT calling close()
                    assertTrue(it.downloaded)
                }
        }
        loadImageAndForgetClose()
        assertTrue(imgFile.exists())
        /**
         * After an Object is considered candidate for garbage collection the cleaner may run.
         * Yet,cleaner run on a different thread, and we may have to sleep a bit to see changes.
         */
        System.gc()
        Thread.sleep(100)
        assertFalse(imgFile.exists())
    }

    @Test
    fun `Even with cleaner delete only once via close`() {
        fun loadImageAndForgetClose() {
            TempImageCleanable(imgUrl)
                .use {
                    assertTrue(it.downloaded)
                }
        }
        loadImageAndForgetClose()
        assertFalse(imgFile.exists()) // Already deleted by call to close from the use block.
        /**
         * Cleaner will not run and NOT call close() for the second time.
         */
        System.gc()
        Thread.sleep(100)
    }
}
