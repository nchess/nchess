package com.github.elementbound.nchess.event;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.BDDMockito.then;
import static org.mockito.MockitoAnnotations.initMocks;

public class EventSourceTest {
    @Mock
    private EventListener<DummyEvent> firstListener;

    @Mock
    private EventListener<DummyEvent> secondListener;

    @Mock
    private EventListener<DummyEvent> thirdListener;

    private EventSource<DummyEvent> eventSource;

    @BeforeMethod
    public void setup() {
        initMocks(this);

        eventSource = new EventSource<>();
    }

    @Test
    public void emitShouldCallAllSubscribers() {
        // given
        eventSource.subscribe(firstListener);
        eventSource.subscribe(secondListener);
        eventSource.subscribe(thirdListener);

        DummyEvent event = new DummyEvent();

        // when
        eventSource.emit(event);

        // then
        then(firstListener).should().on(event);
        then(secondListener).should().on(event);
        then(thirdListener).should().on(event);
    }

    @Test
    public void emitShouldNotCallUnsubscribed() {
        // given
        eventSource.subscribe(firstListener);
        eventSource.subscribe(secondListener);
        eventSource.subscribe(thirdListener);

        eventSource.unsubscribe(thirdListener);

        DummyEvent event = new DummyEvent();

        // when
        eventSource.emit(event);

        // then
        then(firstListener).should().on(event);
        then(secondListener).should().on(event);
        then(thirdListener).shouldHaveZeroInteractions();
    }

    private class DummyEvent {

    }
}