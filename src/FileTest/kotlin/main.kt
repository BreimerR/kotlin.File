import file.File
import kotlin.test.Test
import kotlin.test.assertNotNull

class FileTests {
    @Test
    fun testHello() {
        val file = File("")

        // make sure the file opens correctly
        // create options to create file if file does not exist..
        file.open {
            assertNotNull(file)
        }
    }
}

