package com.targetmusic.core.ports.out.twofa;

public interface TotpEncryptionPort {
    String encrypt(String plaintext);

    String decrypt(String ciphertext);
}
