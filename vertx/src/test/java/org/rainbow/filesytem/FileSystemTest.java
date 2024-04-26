package org.rainbow.filesytem;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileSystemTest {

    private final Vertx vertx = Vertx.vertx();

    String sourcePath = "test.txt";

    private final String desPath = "test.text.copy";

    @Test
    @DisplayName("Copy")
    @Order(1)
    void testFileSystemSyncCopy(VertxTestContext testContext) {
        Handler<AsyncResult<Void>> handler = result -> testContext.verify(() -> {
            assertTrue(result.succeeded());
            testContext.completeNow();
        });
        vertx.fileSystem().copy(sourcePath, desPath,  handler);
    }

    @Test
    @Order(2)
    @DisplayName("Open")
    void testFileSystemOpen(VertxTestContext testContext){
        Handler<AsyncResult<AsyncFile>> handler = result -> testContext.verify(()->{
            assertTrue(result.succeeded());
            final AsyncFile file = result.result();
            file.write(Buffer.buffer().appendString("hello"),res->testContext.verify(()->assertTrue(res.succeeded())));
            Buffer buffer = Buffer.buffer(100);
            file.read(buffer,0,0,100,readResult->testContext.verify(()->{
                assertEquals("hellois a test file",buffer.toString());
                file.close(closeResult->testContext.verify(()->{
                    assertTrue(closeResult.succeeded());
                    testContext.completeNow();
                }));
            }));
        });
        final OpenOptions openOptions = new OpenOptions();
        vertx.fileSystem().open(desPath,openOptions,handler);
    }

    @Test
    @Order(3)
    @DisplayName("Delete")
    void testFileSystemDelete(VertxTestContext testContext) {
        Handler<AsyncResult<Void>> handler = result -> testContext.verify(() -> {
            assertTrue(result.succeeded());
            testContext.completeNow();
        });

        vertx.fileSystem().delete(desPath, handler);
    }

    @Test
    @DisplayName("Read")
    void testFileSystemRead(VertxTestContext testContext) {
        Handler<AsyncResult<Buffer>> handler = result -> testContext.verify(() -> {
            assertTrue(result.succeeded());
            final Buffer buffer = result.result();
            final String text = buffer.toString();
            assertEquals("this is a test file", text);
            testContext.completeNow();
        });

        vertx.fileSystem().readFile(sourcePath, handler);
    }


}