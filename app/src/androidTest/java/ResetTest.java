import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import org.opendeliver.oppia.R;
import org.digitalcampus.oppia.api.MockApiEndpoint;
import org.digitalcampus.oppia.listener.SubmitListener;
import org.digitalcampus.oppia.model.User;
import org.digitalcampus.oppia.task.Payload;
import org.digitalcampus.oppia.task.ResetTask;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import TestRules.DisableAnimationsRule;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
@SmallTest
public class ResetTest {

    @Rule
    public DisableAnimationsRule disableAnimationsRule = new DisableAnimationsRule();


    private CountDownLatch signal;
    private MockWebServer mockServer;
    private Context context;
    private Payload response;

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getTargetContext();
        signal = new CountDownLatch(1);
    }

    @After
    public void tearDown() throws Exception {
        signal.countDown();
        mockServer.shutdown();
    }

    @Test
    public void passwordReset_ResetSuccessful() throws Exception {
        try {
            mockServer = new MockWebServer();

            String filename = "responses/response_201_reset.json";

            mockServer.enqueue(new MockResponse()
                    .setResponseCode(201)
                    .setBody(Utils.FileUtils.getStringFromFile(InstrumentationRegistry.getContext(), filename)));

            mockServer.start();

        }catch(IOException ioe) {
            ioe.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

        ArrayList<Object> users = new ArrayList<>();
        User u = new User();
        u.setUsername("");
        u.setPassword("");
        users.add(u);

        Payload p = new Payload(users);
        try {
            ResetTask task = new ResetTask(context, new MockApiEndpoint(mockServer));
            task.setResetListener(new SubmitListener() {
                @Override
                public void apiKeyInvalidated() { }

                @Override
                public void submitComplete(Payload r) {
                    response = r;
                    signal.countDown();
                }
            });
            task.execute(p);

            signal.await();
            System.out.println(response.getResultResponse());
            assertTrue(response.isResult());
            assertEquals(context.getString(R.string.reset_complete), response.getResultResponse());

        }catch (InterruptedException ie) {
            ie.printStackTrace();
        }catch(Exception e){}



    }

    @Test
    public void passwordReset_WrongUsername() throws Exception {
        try {
            mockServer = new MockWebServer();

            String filename = "responses/response_400_reset.json";

            mockServer.enqueue(new MockResponse()
                    .setResponseCode(400)
                    .setBody(Utils.FileUtils.getStringFromFile(InstrumentationRegistry.getContext(), filename)));

            mockServer.start();

        }catch(IOException ioe) {
            ioe.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

        ArrayList<Object> users = new ArrayList<>();
        User u = new User();
        u.setUsername("");
        u.setPassword("");
        users.add(u);

        Payload p = new Payload(users);
        try {
            ResetTask task = new ResetTask(context, new MockApiEndpoint(mockServer));
            task.setResetListener(new SubmitListener() {
                @Override
                public void apiKeyInvalidated() { }

                @Override
                public void submitComplete(Payload r) {
                    response = r;
                    signal.countDown();
                }
            });
            task.execute(p);

            signal.await();
            System.out.println(response.getResultResponse());
            assertFalse(response.isResult());
            assertEquals(context.getString(R.string.error_reset), response.getResultResponse());

        }catch (InterruptedException ie) {
            ie.printStackTrace();
        }catch(Exception e){}



    }

    @Test
    public void passwordReset_EmptyResponse()throws Exception {
        try {
            mockServer = new MockWebServer();

            mockServer.enqueue(new MockResponse()
                    .setBody(""));

            mockServer.start();

        }catch(IOException ioe) {
            ioe.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

        ArrayList<Object> users = new ArrayList<>();
        User u = new User();
        u.setUsername("");
        u.setPassword("");
        users.add(u);

        Payload p = new Payload(users);
        try {
            ResetTask task = new ResetTask(context, new MockApiEndpoint(mockServer));
            task.setResetListener(new SubmitListener() {
                @Override
                public void apiKeyInvalidated() {  }

                @Override
                public void submitComplete(Payload r) {
                    response = r;
                    signal.countDown();
                }
            });
            task.execute(p);

            signal.await();
            System.out.println(response.getResultResponse());
            assertFalse(response.isResult());
            assertEquals(context.getString(R.string.error_processing_response), response.getResultResponse());

        }catch (InterruptedException ie) {
            ie.printStackTrace();
        }catch(Exception e){}



    }

    @Test
    public void passwordReset_BadResponse()throws Exception {
        try {
            mockServer = new MockWebServer();

            mockServer.enqueue(new MockResponse()
                    .setBody(Utils.FileUtils.getStringFromFile(InstrumentationRegistry.getContext(), "")));

            mockServer.start();

        }catch(IOException ioe) {
            ioe.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

        ArrayList<Object> users = new ArrayList<>();
        User u = new User();
        u.setUsername("");
        u.setPassword("");
        users.add(u);

        Payload p = new Payload(users);
        try {
            ResetTask task = new ResetTask(context, new MockApiEndpoint(mockServer));
            task.setResetListener(new SubmitListener() {
                @Override
                public void apiKeyInvalidated() {  }

                @Override
                public void submitComplete(Payload r) {
                    response = r;
                    signal.countDown();
                }
            });
            task.execute(p);

            signal.await();
            System.out.println(response.getResultResponse());
            assertFalse(response.isResult());
            assertEquals(context.getString(R.string.error_processing_response), response.getResultResponse());

        }catch (InterruptedException ie) {
            ie.printStackTrace();
        }catch(Exception e){}



    }


}
