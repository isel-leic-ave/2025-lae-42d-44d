package pt.isel

import java.awt.image.BufferedImage
import java.io.Closeable
import java.io.File
import java.lang.ref.Cleaner
import java.net.URI
import javax.imageio.ImageIO

class TempImage(url: String) : Closeable {
    val img: BufferedImage
    val downloaded: Boolean
    private val file = File(url.substringAfterLast('/'))
    init {
        if(file.exists()) {
            img = ImageIO.read(file)
            downloaded = false
        } else {
            val conn = URI(url).toURL().openConnection()
            val kind = conn.getHeaderField("Content-Type").substringAfterLast('/')
            conn.getInputStream().use { stream ->
                img = ImageIO.read(stream)
                ImageIO.write(img, kind, file)
                downloaded = true
            }
        }
    }
    override fun close() {
        println("Try deleting file...")
        if(file.exists()) {
            file.delete()
        }
    }

    protected fun finalize() = close()
}

class TempImageCleanable(url: String) : Closeable {
    companion object {
        val cleaner: Cleaner = Cleaner.create()
    }
    val img: BufferedImage
    val downloaded: Boolean
    private val file = File(url.substringAfterLast('/'))

    private val cleanable = cleaner.register(this, object : Runnable {
        /*
         * Duplicate properties to NOT capture a reference to the enclosing
         * object, which will prevent GC to collect that object.
         * The only variable is the url parameter that will be copied.
         */
        private val file = File(url.substringAfterLast('/'))
        override fun run() {
            println("Try deleting file...")
            if(file.exists()) {
                file.delete()
            }
        }
    })
    init {
        if(file.exists()) {
            img = ImageIO.read(file)
            downloaded = false
        } else {
            val conn = URI(url).toURL().openConnection()
            val kind = conn.getHeaderField("Content-Type").substringAfterLast('/')
            conn.getInputStream().use { stream ->
                img = ImageIO.read(stream)
                ImageIO.write(img, kind, file)
                downloaded = true
            }
        }
    }

    override fun close() {
        cleanable.clean()
    }
}
