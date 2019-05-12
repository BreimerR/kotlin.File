package file


fun main() {
    val f = File("/opt/Projects/Kotlin/File/src/test/me.txt")

    f.open {
        print(it.size)
    }
}
