package ca.abelaid.url.shortener;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ShortenerServiceTest {
    @InjectMocks
    private ShortenerService tested;
    @Mock
    private ShortenedUrlRepository mockShortenedUrlRepository;

    @Mock
    private ShortenerProperties mockShortenerProperties;

    private ListAppender<ILoggingEvent> logWatcher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockShortenerProperties.tokenMaxLength()).thenReturn(2);
        logWatcher = new ListAppender<>();
        logWatcher.start();
        ((Logger) LoggerFactory.getLogger(ShortenerService.class)).addAppender(logWatcher);

    }

    @AfterEach
    void tearDown() {
        ((Logger) LoggerFactory.getLogger(ShortenerService.class)).detachAndStopAllAppenders();

    }

    @Test
    void shouldGetCompleteUrl() throws Exception {
        ShortenedUrlEntity shortenedUrlEntity = ShortenedUrlEntity.builder().token("token").completeUrl("http://original.junit").build();
        when(mockShortenedUrlRepository.findById("token")).thenReturn(Optional.of(shortenedUrlEntity));

        String completeUrl = tested.getCompleteUrl("token");

        verify(mockShortenedUrlRepository).findById("token");
        assertThat(completeUrl).isEqualTo("http://original.junit");

    }

    @Test
    void shouldNotGetCompleteUrlForUnknownToken() {
        when(mockShortenedUrlRepository.findById("token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tested.getCompleteUrl("token")).isInstanceOf(ShortenedUrlNotFoundException.class);

        verify(mockShortenedUrlRepository).findById("token");

    }

    @Test
    void shouldNotShortenAlreadyShortenedUrl() throws Exception {
        ShortenedUrlEntity shortenedUrlEntity = ShortenedUrlEntity.builder().token("token").completeUrl("http://original.junit").build();
        when(mockShortenedUrlRepository.findByCompleteUrl("http://original.junit")).thenReturn(Optional.of(shortenedUrlEntity));

        String token = tested.shorten(URI.create("http://original.junit").toURL());

        verify(mockShortenedUrlRepository).findByCompleteUrl("http://original.junit");
        verifyNoMoreInteractions(mockShortenedUrlRepository, mockShortenerProperties);

        assertThat(token).isEqualTo("token");
    }

    @Test
    void shouldShortenUrl() throws Exception {
        when(mockShortenedUrlRepository.findByCompleteUrl("http://original.junit")).thenReturn(Optional.empty());

        String token = tested.shorten(URI.create("http://original.junit").toURL());

        verify(mockShortenedUrlRepository).findByCompleteUrl("http://original.junit");
        verify(mockShortenerProperties).tokenMaxLength();

        ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockShortenedUrlRepository).persist(tokenCaptor.capture(), eq("http://original.junit"));
        verifyNoMoreInteractions(mockShortenedUrlRepository, mockShortenerProperties);

        assertThat(token).isEqualTo(tokenCaptor.getValue());
    }

    @Test
    void shouldRetryShortenUrlIfRandomTokenAlreadyExists() throws Exception {
        when(mockShortenedUrlRepository.findByCompleteUrl("http://original.junit")).thenReturn(Optional.empty());
        when(mockShortenerProperties.randomMaxRetry()).thenReturn(1);

        final AtomicBoolean firstCall = new AtomicBoolean(true);
        doAnswer(invocationOnMock -> {
            if (firstCall.get()) {
                firstCall.set(false);
                throw new DataIntegrityViolationException("hey");
            }
            return null;
        }).when(mockShortenedUrlRepository).persist(anyString(), eq("http://original.junit"));

        String token = tested.shorten(URI.create("http://original.junit").toURL());

        verify(mockShortenedUrlRepository).findByCompleteUrl("http://original.junit");
        verify(mockShortenerProperties, times(2)).tokenMaxLength();
        verify(mockShortenerProperties).randomMaxRetry();

        ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockShortenedUrlRepository, times(2)).persist(tokenCaptor.capture(), eq("http://original.junit"));
        verifyNoMoreInteractions(mockShortenedUrlRepository, mockShortenerProperties);

        assertThat(token).isEqualTo(tokenCaptor.getAllValues().get(1));
    }

    @Test
    void shouldRetryShortenUrlIfRandomTokenAlreadyExistsUntilMaxRetry() throws Exception {
        when(mockShortenedUrlRepository.findByCompleteUrl("http://original.junit")).thenReturn(Optional.empty());
        when(mockShortenerProperties.randomMaxRetry()).thenReturn(2);
        doThrow(DataIntegrityViolationException.class).when(mockShortenedUrlRepository).persist(anyString(), eq("http://original.junit"));

        String token = tested.shorten(URI.create("http://original.junit").toURL());

        verify(mockShortenedUrlRepository).findByCompleteUrl("http://original.junit");
        verify(mockShortenedUrlRepository, times(3)).persist(anyString(), eq("http://original.junit"));
        verify(mockShortenerProperties, times(3)).tokenMaxLength();
        verify(mockShortenerProperties, times(3)).randomMaxRetry();
        verifyNoMoreInteractions(mockShortenedUrlRepository, mockShortenerProperties);

        // FIXME impl should throw exception
        assertThat(token).isNull();
        assertThat(logWatcher.list).hasSize(3);
        assertThat(logWatcher.list.get(0).getFormattedMessage()).isEqualTo("Retrying because of collision");
        assertThat(logWatcher.list.get(0).getLevel()).isEqualTo(Level.WARN);
        assertThat(logWatcher.list.get(1).getFormattedMessage()).isEqualTo("Retrying because of collision");
        assertThat(logWatcher.list.get(1).getLevel()).isEqualTo(Level.WARN);
        assertThat(logWatcher.list.get(2).getFormattedMessage()).isEqualTo("Max attempt reached");
        assertThat(logWatcher.list.get(2).getLevel()).isEqualTo(Level.ERROR);
    }
}


