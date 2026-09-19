package org.sopt.routee.member.internal.converter;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.sopt.routee.member.internal.config.MemberOAuthCredentialProperty;
import org.sopt.routee.member.internal.exception.OAuthCredentialEncryptionException;
import org.springframework.stereotype.Component;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
@Component
public class RefreshTokenAttributeConverter implements AttributeConverter<String, String> {

	private static final String TRANSFORMATION = "AES/GCM/NoPadding";
	private static final String KEY_ALGORITHM = "AES";
	private static final int IV_LENGTH_BYTES = 12;
	private static final int TAG_LENGTH_BITS = 128;

	private final SecretKeySpec secretKey;
	private final SecureRandom secureRandom = new SecureRandom();

	public RefreshTokenAttributeConverter(MemberOAuthCredentialProperty property) {
		this.secretKey = new SecretKeySpec(Base64.getDecoder().decode(property.encryptionKey()), KEY_ALGORITHM);
	}

	@Override
	public String convertToDatabaseColumn(String attribute) {
		if (attribute == null) {
			return null;
		}

		try {
			byte[] iv = new byte[IV_LENGTH_BYTES];
			secureRandom.nextBytes(iv);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
			byte[] cipherText = cipher.doFinal(attribute.getBytes(StandardCharsets.UTF_8));

			byte[] encrypted = new byte[iv.length + cipherText.length];
			System.arraycopy(iv, 0, encrypted, 0, iv.length);
			System.arraycopy(cipherText, 0, encrypted, iv.length, cipherText.length);

			return Base64.getEncoder().encodeToString(encrypted);
		} catch (GeneralSecurityException e) {
			throw new OAuthCredentialEncryptionException(e);
		}
	}

	@Override
	public String convertToEntityAttribute(String dbData) {
		if (dbData == null) {
			return null;
		}

		try {
			byte[] decoded = Base64.getDecoder().decode(dbData);
			byte[] iv = Arrays.copyOfRange(decoded, 0, IV_LENGTH_BYTES);
			byte[] cipherText = Arrays.copyOfRange(decoded, IV_LENGTH_BYTES, decoded.length);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
			byte[] plainText = cipher.doFinal(cipherText);

			return new String(plainText, StandardCharsets.UTF_8);
		} catch (GeneralSecurityException | IllegalArgumentException e) {
			throw new OAuthCredentialEncryptionException(e);
		}
	}
}
