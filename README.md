# DES Encryption System (Java)

A from-scratch, educational implementation of the **DES (Data Encryption Standard)** block cipher in Java. It implements the full Feistel structure — initial/final permutation, key scheduling (PC-1/PC-2), the expansion and S-box substitution rounds, and the straight permutation — with an interactive console application for encrypting/decrypting text and files, plus optional email delivery of ciphertext via SMTP.

> ⚠️ **Educational project.** DES is a legacy cipher with a 56-bit effective key length that is considered cryptographically broken/insecure for real-world use (brute-forceable with modern hardware). This project exists to demonstrate how DES works internally — it is **not** suitable for protecting sensitive data. See [Security Notes](#security-notes).

## Features

- **Full DES algorithm implemented from first principles**
  - Key schedule: PC-1 / PC-2 permuted choice tables, left circular shifts, 16 round sub-keys
  - Initial Permutation (IP) and Final Permutation (FP)
  - Feistel round function: expansion (E-table), XOR with round key, S-box substitution (S1–S8), straight permutation (P-table)
- **Interactive CLI** (`DESApplication`) for:
  - Viewing the full key schedule (all 16 round sub-keys, binary + hex)
  - Encrypting/decrypting text (plain text or hex input)
  - Encrypting/decrypting files (`.txt`, `.docx`, `.jpg`, `.png`, `.pdf`, or any other file type)
  - Generating a random 56-bit DES key
  - Loading a key manually, from file, or reusing the active session key
- **Email integration** (JavaMail) to send ciphertext — as a message body and/or with the encrypted file attached — to a recipient

## Project Structure

```
├── DESKeyGeneration.java  # Core DES engine: key schedule, IP/FP, Feistel rounds, S-boxes, file/text encrypt-decrypt
├── DESApplication.java    # Interactive command-line menu tying everything together
├── EmailSender.java       # SMTP email sending (ciphertext + optional file attachment) via JavaMail
└── Main.java              # Minimal alternate entry point (work in progress)
```

## Requirements

- **Java 17+** (uses switch expressions / arrow syntax)
- **JavaMail (javax.mail) and JAF (javax.activation)** on the classpath — only required for the email-sending feature (menu option 6)

## Getting Started

### 1. Compile

```bash
# Adjust classpath to wherever your JavaMail/Activation jars live
javac -d out -cp "lib/*" *.java
```

### 2. Run

```bash
java -cp "out:lib/*" DESApplication
```

You'll see an interactive menu:

```
=======================================================
   Enhanced DES Secure Communication System
=======================================================
-------------------------------------------------------
  Active Key : (none – please generate or enter a key)
-------------------------------------------------------
  1. Key Expansion
  2. Encrypt Text
  3. Decrypt Text
  4. Encrypt File
  5. Decrypt File
  6. Send Ciphertext via Email
  7. Generate Random DES Key
  8. Exit
-------------------------------------------------------
```

### Typical Workflow

1. **Generate or load a key** (option 7 for random, or option 1/2/4 will prompt you to load one) — accepts a 16-character hex string (e.g. `133457799BBCDFF1`) or up to 8 characters of plain text.
2. **View the key schedule** (option 1) to see all 16 round sub-keys derived via PC-1/PC-2 and the left shifts.
3. **Encrypt text or a file** (options 2 / 4) — text can be entered as plain text or hex; files are encrypted to a `.des` output file.
4. **Decrypt** (options 3 / 5) using the same key.
5. *(Optional)* **Email the ciphertext** (option 6), with or without the encrypted file attached, via SMTP (Gmail recommended, using an App Password).

## Security Notes

This project is meant for **learning and demonstration**, not production cryptography:

- **DES's 56-bit key space is considered insecure** by modern standards and can be brute-forced with contemporary hardware. Use it only to study the algorithm, not to protect real data.
- **No authentication/integrity** is provided (no MAC or AEAD) — ciphertext can be tampered with undetected.
- **Email credentials**: `EmailSender` accepts plaintext SMTP credentials (or App Passwords) at runtime and passes them to `Authenticator`. Never hard-code real credentials in source control; use environment variables or a secrets manager instead.
- This implementation has not been checked for timing side-channels and should not be considered constant-time.

For real-world encryption needs, use a modern, vetted algorithm — e.g. AES-GCM via `javax.crypto.Cipher`, or a well-audited library — rather than DES.

## License

Add your preferred license here (e.g., MIT).

## Contributing

Issues and pull requests are welcome — this is intended as a learning resource, so clarifications, bug fixes, and additional test vectors are especially appreciated.
