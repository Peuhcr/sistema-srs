package com.example.service;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


public final class PasswordHasher {

    private static final String PREFIXO = "pbkdf2_sha256";
    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACOES = 310_000;
    private static final int TAMANHO_SALT = 16;
    private static final int TAMANHO_HASH_BITS = 256;

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    public String gerarHash (String senha) {

        byte[] salt = new byte[TAMANHO_SALT];
        RANDOM.nextBytes(salt);

        byte[] hash = derivar(senha, salt, ITERACOES, TAMANHO_HASH_BITS);

        return PREFIXO + "$" + ITERACOES + "$"
                + ENCODER.encodeToString(salt) + "$"
                + ENCODER.encodeToString(hash);
    }

    public boolean verificar (String senha, String armazenado) {

        if (senha == null || armazenado == null) {
            return false;
        }

        String[] partes = armazenado.split("\\$");

        if (partes.length != 4 || !PREFIXO.equals(partes[0])) {
            return false;
        }

        int iteracoes = Integer.parseInt(partes[1]);
        byte[] salt = DECODER.decode(partes[2]);
        byte[] esperado = DECODER.decode(partes[3]);

        byte[] calculado = derivar(senha, salt, iteracoes, esperado.length * 8);

        return MessageDigest.isEqual(esperado, calculado);
    }

    private byte[] derivar (String senha, byte[] salt, int iteracoes, int bits) {

        PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), salt, iteracoes, bits);

        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();

        } catch (GeneralSecurityException e) {

            throw new IllegalStateException("Algoritmo de hash indisponível: " + ALGORITMO, e);

        } finally {

            spec.clearPassword();
        }
    }
}
