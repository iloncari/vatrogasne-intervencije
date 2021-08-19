/*
 * ChangeBroadcaster ChangeBroadcaster.java.
 *
 */
package hr.tvz.vi.event;

import com.google.common.eventbus.AsyncEventBus;

import java.util.EventObject;
import java.util.concurrent.Executors;

import lombok.experimental.UtilityClass;

/**
 * The Class ChangeBroadcaster.
 *
 * @author Igor Lončarić (iloncari2@tvz.hr)
 * @since 11:07:02 AM Aug 11, 2021
 */
@UtilityClass
public final class ChangeBroadcaster {

  /** The Constant PUSH_EVENT_BUS. */
  private static final AsyncEventBus PUSH_EVENT_BUS = new AsyncEventBus("vi-push-event-bus", Executors.newCachedThreadPool());

  /**
   * Register to push.
   *
   * @param listener the listener
   */
  public static synchronized void registerToPushEvents(final Object listener) {
    PUSH_EVENT_BUS.register(listener);
  }

  /**
   * Unregister from push.
   *
   * @param listener the listener
   */
  public static synchronized void unregisterFromPushEvents(final Object listener) {
    try {
      PUSH_EVENT_BUS.unregister(listener);
    } catch (final IllegalArgumentException e) {
      // do nothing as already unregistered
    }
  }

  /**
   * Fire push event.
   *
   * @param event the event
   */
  public static synchronized void firePushEvent(final EventObject event) {
    PUSH_EVENT_BUS.post(event);
  }

  /**
   * Register to push.
   *
   * @param listener the listener
   */
  public static synchronized void registerToSessionEvents(final Object listener, final AsyncEventBus eventBus) {
    eventBus.register(listener);
  }

  /**
   * Unregister from push.
   *
   * @param listener the listener
   */
  public static synchronized void unregisterFromSessionEvents(final Object listener, final AsyncEventBus eventBus) {
    try {
      eventBus.unregister(listener);
    } catch (final IllegalArgumentException e) {
      // do nothing as already unregistered
    }
  }

  /**
   * Fire push event.
   *
   * @param event the event
   */
  public static synchronized void fireSessionEvent(final EventObject event, final AsyncEventBus eventBus) {
    eventBus.post(event);
  }

}
