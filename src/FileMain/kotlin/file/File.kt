package file

import kotlinx.cinterop.CPointer
import lib.math.charVal
import lib.math.long
import lib.oop.Class
import lib.oop.StaticClass
import platform.posix.*


class FileStatic : StaticClass() {
    operator fun invoke(fullFilePathWithExtension: String, dirSeparator: String = "/"): FileClass = FileClass(fullFilePathWithExtension)
}


class FileClass(private val fullFilePathWithExtension: String) : Class<FileStatic>() {
    override val self = File

    operator fun invoke() {

    }

    var start = 0
    var end = SEEK_END
    var i = 0

    var file: CPointer<FILE>? = null

    val nextChar: Char
        get () {
            val char = fgetc(file).charVal
            // update current char index
            i += 1

            if (char == (-1).charVal) atEnd = true

            return char
        }

    val prevChar: Char
        get() {
            fseek(file, -1, i)
            val char = fgetc(file).charVal
            fseek(file, 1, SEEK_SET)
            atEnd = false
            return char
        }

    val isOpen: Boolean
        get() = file != null

    var atEnd: Boolean = false


    fun open(mode: String = "r") {
        file = fopen(fullFilePathWithExtension, mode)

        if (file == null) throw FileError("File $fullFilePathWithExtension does not exist")
    }

    fun open(mode: String = "r", action: FileClass.() -> Unit) {
        open(mode)
        // this will never be null since we are throwing error  file not found
        this.apply(action)
    }


    fun safeOpen(mode: String = "r"): Boolean {
        return try {
            open(mode)
            true
        } catch (e: FileError) {
            false
        }
    }

    fun close(): Boolean {
        return if (isOpen) {
            fclose(file)
            true
        } else false
    }


    // SEEK_SET = beginning of the file
    fun moveCursor(to: Int, from: Int = SEEK_SET) {
        fseek(file, from.long, to)


        if (atEnd) {
            if (to < i) {
                atEnd = false
            }
        }

        i = to
    }

    fun cursorAt(position: Int) {
        moveCursor(position, start)
    }


    fun getChars(from: Int, to: Int): Array<Char> {
        cursorAt(from)

        var chars = arrayOf<Char>()


        for (i in 0 until (to - from)) {
            val char = nextChar
            chars += char
        }

        return chars
    }

    val size: Long
        get() {
            moveCursor(end)

            val s = cursorPos

            moveCursor(start)

            return s
        }

    val fileSize: FileSizeClass
        get() {
            return FileSize(size)
        }

    private val cursorPos: Long
        get() {
            return ftell(file)
        }

    override fun toString(): String {
        var content = "";

        while (!atEnd) {
            content += nextChar
        }

        return content;
    }
}


val File = FileStatic()