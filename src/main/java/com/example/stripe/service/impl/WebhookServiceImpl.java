package com.example.stripe.service.impl;

import static com.example.stripe.constant.Constants.PAYMENT_INTENT_SUCCESS;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.stripe.entity.Coin;
import com.example.stripe.entity.SessionRecord;
import com.example.stripe.entity.Wallet;
import com.example.stripe.repository.CoinRepository;
import com.example.stripe.repository.SessionRecordRepository;
import com.example.stripe.repository.WalletRepository;
import com.example.stripe.service.WebhookService;
import com.example.stripe.service.dto.request.WebhookEventRequestDto;
import static com.example.stripe.constant.Constants.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WebhookServiceImpl implements WebhookService {

    private final SessionRecordRepository recordRepository;
    private final WalletRepository walletRepository;
    private final CoinRepository coinRepository;

    @Override
    @Transactional
    public void handleWebhookRequest(WebhookEventRequestDto request) {
        switch (request.getType()) {
            case PAYMENT_INTENT_SUCCESS:
                Optional<SessionRecord> sessionRecordOpt = recordRepository.findByPaymentIntentId(
                        request.getData().getObject().getId());

                if (sessionRecordOpt.isEmpty())
                    return;

                Optional<Wallet> walletOpt = walletRepository.findByUserId(sessionRecordOpt.get().getUser().getId());

                if (walletOpt.isEmpty()) {
                    Optional<Coin> coinOpt = coinRepository.findByShortName(USDC_SHORTNAME);
                    if (coinOpt.isEmpty()) {
                        String curency = request.getData().getObject().getCurrency();
                        switch (curency) {
                            case "usd":
                                BigDecimal exchangeRate = BigDecimal.ONE;
                                coinRepository.save(Coin.builder()
                                        .name(USDC_NAME)
                                        .shortName(USDC_SHORTNAME)
                                        .exchangeRate(exchangeRate)
                                        .build());
                                break;

                            default:
                                break;
                        }
                    }
                    coinOpt = coinRepository.findByShortName(USDC_SHORTNAME);

                    walletRepository.save(Wallet.builder()
                            .balance(request.getData().getObject().getAmount().divide(BigDecimal.valueOf(100)))
                            .blockedBalance(BigDecimal.ZERO)
                            .user(sessionRecordOpt.get().getUser())
                            .coin(coinOpt.get())
                            .build());
                } else {
                    walletOpt.get().setBalance(walletOpt.get().getBalance()
                            .add(request.getData().getObject().getAmount().divide(BigDecimal.valueOf(100))));
                }
                break;
            default:
                break;
        }
    }
}
