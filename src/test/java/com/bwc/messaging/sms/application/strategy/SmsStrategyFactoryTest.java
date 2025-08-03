package com.bwc.messaging.sms.application.strategy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bwc.messaging.shared.domain.MessageChannel;

@ExtendWith(MockitoExtension.class)
@DisplayName("SmsStrategyFactory 테스트")
class SmsStrategyFactoryTest {

    @Mock
    private SmsStrategy lguV1Strategy;
    
    @Mock
    private SmsStrategy lguV2Strategy;
    
    @Mock
    private SmsStrategy mtsStrategy;
    
    private SmsStrategyFactory smsStrategyFactory;
    
    @BeforeEach
    void setUp() {
        // Mock 설정
        when(lguV1Strategy.getSupportedChannel()).thenReturn(MessageChannel.LGU_V1);
        when(lguV2Strategy.getSupportedChannel()).thenReturn(MessageChannel.LGU_V2);
        when(mtsStrategy.getSupportedChannel()).thenReturn(MessageChannel.MTS);
        
        List<SmsStrategy> strategies = List.of(lguV1Strategy, lguV2Strategy, mtsStrategy);
        smsStrategyFactory = new SmsStrategyFactory(strategies);
    }
    
    @Test
    @DisplayName("LGU V1 전략 조회 성공")
    void getStrategy_LguV1_Success() {
        // When
        SmsStrategy strategy = smsStrategyFactory.getStrategy(MessageChannel.LGU_V1);
        
        // Then
        assertNotNull(strategy);
        assertEquals(lguV1Strategy, strategy);
        assertEquals(MessageChannel.LGU_V1, strategy.getSupportedChannel());
    }
    
    @Test
    @DisplayName("LGU V2 전략 조회 성공")
    void getStrategy_LguV2_Success() {
        // When
        SmsStrategy strategy = smsStrategyFactory.getStrategy(MessageChannel.LGU_V2);
        
        // Then
        assertNotNull(strategy);
        assertEquals(lguV2Strategy, strategy);
        assertEquals(MessageChannel.LGU_V2, strategy.getSupportedChannel());
    }
    
    @Test
    @DisplayName("MTS 전략 조회 성공")
    void getStrategy_Mts_Success() {
        // When
        SmsStrategy strategy = smsStrategyFactory.getStrategy(MessageChannel.MTS);
        
        // Then
        assertNotNull(strategy);
        assertEquals(mtsStrategy, strategy);
        assertEquals(MessageChannel.MTS, strategy.getSupportedChannel());
    }
    
    @Test
    @DisplayName("지원하지 않는 채널에 대한 예외 발생")
    void getStrategy_UnsupportedChannel_ThrowsException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> smsStrategyFactory.getStrategy(MessageChannel.SMTP)
        );
        
        assertEquals("No strategy found for channel: SMTP", exception.getMessage());
    }
    
    @Test
    @DisplayName("모든 전략이 건강한 경우 사용 가능한 채널 조회")
    void getAvailableChannels_AllHealthy() {
        // Given
        when(lguV1Strategy.isHealthy()).thenReturn(true);
        when(lguV2Strategy.isHealthy()).thenReturn(true);
        when(mtsStrategy.isHealthy()).thenReturn(true);
        
        // When
        List<MessageChannel> availableChannels = smsStrategyFactory.getAvailableChannels();
        
        // Then
        assertEquals(3, availableChannels.size());
        assertTrue(availableChannels.contains(MessageChannel.LGU_V1));
        assertTrue(availableChannels.contains(MessageChannel.LGU_V2));
        assertTrue(availableChannels.contains(MessageChannel.MTS));
    }
    
    @Test
    @DisplayName("일부 전략이 비정상인 경우 사용 가능한 채널 조회")
    void getAvailableChannels_SomeUnhealthy() {
        // Given
        when(lguV1Strategy.isHealthy()).thenReturn(true);
        when(lguV2Strategy.isHealthy()).thenReturn(false);
        when(mtsStrategy.isHealthy()).thenReturn(true);
        
        // When
        List<MessageChannel> availableChannels = smsStrategyFactory.getAvailableChannels();
        
        // Then
        assertEquals(2, availableChannels.size());
        assertTrue(availableChannels.contains(MessageChannel.LGU_V1));
        assertFalse(availableChannels.contains(MessageChannel.LGU_V2));
        assertTrue(availableChannels.contains(MessageChannel.MTS));
    }
    
    @Test
    @DisplayName("모든 전략이 비정상인 경우 빈 채널 목록")
    void getAvailableChannels_AllUnhealthy() {
        // Given
        when(lguV1Strategy.isHealthy()).thenReturn(false);
        when(lguV2Strategy.isHealthy()).thenReturn(false);
        when(mtsStrategy.isHealthy()).thenReturn(false);
        
        // When
        List<MessageChannel> availableChannels = smsStrategyFactory.getAvailableChannels();
        
        // Then
        assertTrue(availableChannels.isEmpty());
    }
    
    @Test
    @DisplayName("빈 전략 목록으로 팩토리 생성")
    void constructor_EmptyStrategyList() {
        // Given
        List<SmsStrategy> emptyStrategies = List.of();
        
        // When
        SmsStrategyFactory emptyFactory = new SmsStrategyFactory(emptyStrategies);
        
        // Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> emptyFactory.getStrategy(MessageChannel.LGU_V1)
        );
        
        assertEquals("No strategy found for channel: LGU_V1", exception.getMessage());
    }
}