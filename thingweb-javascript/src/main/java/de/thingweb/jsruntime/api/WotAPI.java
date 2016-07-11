package de.thingweb.jsruntime.api;

import de.thingweb.client.ClientFactory;
import de.thingweb.client.UnsupportedException;
import de.thingweb.desc.ThingDescriptionParser;
import de.thingweb.discovery.TDRepository;
import de.thingweb.jsruntime.JsPromise;
import de.thingweb.servient.ServientBuilder;
import de.thingweb.servient.ThingInterface;
import de.thingweb.servient.ThingServer;
import de.thingweb.thing.Thing;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * Created by Johannes on 09.12.2015.
 */
public class WotAPI {
    public static final String version = "0.0.2";
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    
    private HashMap<Integer, JavaScriptTimeoutTask> tasks =
			new HashMap<Integer, JavaScriptTimeoutTask>(); // tasks and their id
	private Timer timer = new Timer("WoT-timer", true); // timer to schedule tasks
	private int timernr = 1; // increasing task counter

    public String getVersion() {
        return version;
    }

    private static ClientFactory cf;
    private static ClientFactory getClientFactory() {
        if(cf == null)
            cf = new ClientFactory();
        return cf;
    }

    private ThingServer thingServer;
    private ThingServer getThingServer() {
        if(thingServer == null) {
            try {
                thingServer = ServientBuilder.newThingServer();
                ServientBuilder.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return thingServer;
    }

    public WotAPI() {

    }

    public WotAPI(ThingServer thingServer) {
        this.thingServer = thingServer;
    }

    public JsPromise discover(String method, ScriptObjectMirror filter) {
        JsPromise promise = new JsPromise();

        executor.submit(() -> {
                if(method.equals("registry")) {
                    String name = null;
                    if(filter.containsKey("name")) {
                        name = (String) filter.getMember("name");
                    } else {
                        promise.reject(new RuntimeException("No name given for registry-based discovery"));
                        return;
                    }

                    String registry;
                    if(filter.containsKey("registry")) {
                        registry = (String) filter.getMember("registry");
                    } else {
                        promise.reject(new RuntimeException("No registry given for registry-based discovery"));
                        return;
                    }
                        try {
                            final TDRepository repo = new TDRepository(registry);
                            final JSONObject jsonObject = repo.tdFreeTextSearch(name);
                            promise.resolve(jsonObject);
                        } catch (Exception e) {
                            promise.reject(e);
                            return;
                        }
                } else if(method.equals("local")) {
                    if(filter.containsKey("name")) {
                        final String name = (String) filter.getMember("name");
                        final ExposedThing localThing = getLocalThing(name);
                        final ExposedThing[] things = {localThing};
                        promise.resolve(things);
                    } else {
                        promise.reject(new RuntimeException("No name given for local discovery"));
                    }
                }
        });

        return promise;
    }

    public JsPromise consumeDescriptionUri(String uri) {
        JsPromise promise = new JsPromise();

        executor.submit(() -> {
            try {
                promise.resolve(
                        ConsumedThing.from(
                                getClientFactory().getClientUrl(new URI(uri))
                        )
                );
            } catch (IOException | UnsupportedException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });

        return promise;
    }

    public JsPromise consumeDescription(String jsonld) {
        JsPromise promise = new JsPromise();

        executor.submit(() -> {
            try {
                Thing description = ThingDescriptionParser.fromBytes(jsonld.getBytes());
                promise.resolve(
                        ConsumedThing.from(
                                getClientFactory().getClientFromTD(description)
                        )
                );
            } catch (IOException | UnsupportedException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });

        return promise;
    }

    public JsPromise createFromDescription(String jsonld)  {
        JsPromise promise = new JsPromise();

        executor.submit(() -> {
            try {
                final Thing thing = ThingDescriptionParser.fromBytes(jsonld.getBytes());
                final ThingInterface servedThing = getThingServer().addThing(thing);
                final ExposedThing exposedThing = ExposedThing.from(servedThing, getThingServer());
                promise.resolve(exposedThing);
            } catch (IOException e) {
                promise.reject(new RuntimeException(e));
            }
        });

        return promise;
    }

    public JsPromise createFromDescriptionUri(String uri)  {
        JsPromise promise = new JsPromise();

        executor.submit(() -> {
            try {
                final Thing thing = ThingDescriptionParser.fromURL(new URL(uri));
                final ThingInterface servedThing = getThingServer().addThing(thing);
                final ExposedThing exposedThing = ExposedThing.from(servedThing, getThingServer());
                promise.resolve(exposedThing);
            } catch (IOException e) {
                promise.reject(new RuntimeException(e));
            }
        });

        return promise;
    }


    public JsPromise newThing(String name) {
        JsPromise promise = new JsPromise();

        executor.submit(() -> {
            final Thing thing = new Thing(name);
            final ThingInterface servedThing = getThingServer().addThing(thing);
            final ExposedThing exposedThing = ExposedThing.from(servedThing, getThingServer());
            promise.resolve(exposedThing);
        });

        return promise;
    }


    public ExposedThing getLocalThing(String name) {
        final ThingInterface thing = getThingServer().getThing(name);
        return ExposedThing.from(thing,getThingServer());
    }
    

	/**
	 * Creates a new Task for the specified function and the specified arguments
	 * and adds it to the timer.
	 * 
	 * @param function
	 *            the function to call
	 * @param millis
	 *            the time until the function is called in milliseconds
	 * @param args
	 *            the arguments for the function
	 * @return the id of the created task
	 */
	public synchronized int setTimeout(Function function, long millis, Object... args) {
		if (function == null)
			throw new NullPointerException("WoT.setTimeout expects function not null");
		int nr = timernr++;
		JavaScriptTimeoutTask task = new JavaScriptTimeoutTask(function, args);
		tasks.put(nr, task);
		timer.schedule(task, millis);
		return nr;
	}

	/**
	 * Cancels the specified task
	 * 
	 * @param id
	 *            the task's id
	 */
	public synchronized void clearTimeout(int id) {
		JavaScriptTimeoutTask task = tasks.get(id);
		if (task != null) {
			task.cancel();
		}
	}

	/**
	 * Creates a new Task for the specified function and the specified arguments
	 * and adds it to the timer. The timer executes it subsequentially after the
	 * specified amount of time.
	 * 
	 * @param function
	 *            the function to call
	 * @param millis
	 *            the time until the function is called in milliseconds
	 * @param args
	 *            the arguments for the function
	 * @return the id of the created task
	 */
	public synchronized int setInterval(Function function, long millis, Object... args) {
		if (function == null)
			throw new NullPointerException("app.setInterval expects function not null");
		int nr = timernr++;
		JavaScriptTimeoutTask task = new JavaScriptTimeoutTask(function, args);
		tasks.put(nr, task);
		timer.scheduleAtFixedRate(task, millis, millis);
		return nr;
	}

	/**
	 * Cancels the specified task
	 * 
	 * @param id
	 *            the task's id
	 */
	public synchronized void clearInterval(int id) {
		clearTimeout(id);
	}
	
	/**
	 * When app.setTimeout is called a new task for a specified function is
	 * created and added to the timer. After the specified milliseconds have
	 * passed this task is executed and adds the function to the worker
	 * queue of the app.The app's thread executes this runnable and calls
	 * the specified function.
	 */
	private class JavaScriptTimeoutTask extends TimerTask {
		
		private Function function; // the function
		private Object[] args; // the arguments
		
		public JavaScriptTimeoutTask(Function function, Object[] args) {
			this.function = function;
			this.args = args;
		}

		/**
		 * This function is called from the timer after the specified amount
		 * of time has passed. It then adds a Runnable to the apps worker
		 * queue. The app's thread executes this runnable and calls the
		 * specified function.
		 */
		@Override
		public void run() {
			// add function to working queue
			executor.submit(new FunctionExecuter());
		}

		
		/**
		 * FunctionExecuter only wraps the function call. Therefore it can
		 * be added to the app's worker queue.
		 */
		private class FunctionExecuter implements Runnable {
			public void run() {
				function.apply(args);
			}
		}
	}
}
