package com.chatop.rental_backend.lib;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.tika.io.FilenameUtils;
import org.apache.tika.parser.txt.CharsetDetector;

import com.chatop.rental_backend.exception.exceptions.ServerErrorException;
import com.chatop.rental_backend.model.Model;

/**
 * This class provides file storage strategy and filename cleaner
 *
 * The storage strategy will be using date. The file name may contain sufix timestamp highresolution
 * time (hrtime or microtime) if is duplicate. (function() { return time() . ' ' .
 * Str::slug(microtime(true)) . ' ' . hrtime(true); })();
 *
 * posts/ - The model table name 10/ - Year 04/ - Month 19/ - Day 5f/ - First two letters of md5
 * file black-cat-under-red-car.png - Slugified file name
 * black-cat-under-red-car-286071690833400.png - Slugified file name that is duplicated
 *
 * The final path will be
 * storage/modelfiles/posts/10/04/19/5f/black-cat-under-red-car-286071690833400.png
 *
 * ! No limits of file per folder will be handled.
 */
public final class FileStorageHelper {
  protected FileStorageHelper() {}

  public static String beautifyFileName(String filename) {
    // Reduce consecutive characters.
    filename = filename.replaceAll(" +", "-"); // "file name.zip" becomes 'file-name.zip'
    filename = filename.replaceAll("_+", "-"); // "file___name.zip" becomes 'file-name.zip'
    filename = filename.replaceAll("-+", "-"); // "file---name.zip" becomes 'file-name.zip'

    filename = filename.replaceAll("-*\\.-*", "."); // "file--.--.-.--name.zip" becomes
                                                    // "file.name.zip"
    filename = filename.replaceAll("\\.{2,}", "."); // "file...name..zip" becomes "file.name.zip"

    // Lowercase for windows/unix interoperability.
    filename = filename.toLowerCase();

    // Trim leading and trailing '.' and '-'
    filename = filename.replaceAll("^[.-]+|[.-]+$", ""); // ".file-name.-" becomes "file-name"

    return filename;
  }

  /** Get the storage path for a given entity filename */
  public static <M extends Model> Path getStoragePath(final Class<M> entity,
      final String filename) {
    final var filenameHash = md5(filename);
    final var pathFolder = String.format("%s/%s/%s/%s/%s", entity.getSimpleName().toLowerCase(),
        new SimpleDateFormat("yy").format(new Date()),
        new SimpleDateFormat("MM").format(new Date()),
        new SimpleDateFormat("dd").format(new Date()), filenameHash.substring(0, 2));
    return Path.of(pathFolder);
  }

  /** Create a md5 sum of a string */
  public static String md5(final String input) {
    try {
      final var md = MessageDigest.getInstance("MD5");
      final var messageDigest = md.digest(input.getBytes());
      final var hexString = new StringBuilder();
      for (final byte b : messageDigest) {
        final var hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (final NoSuchAlgorithmException e) {
      throw new ServerErrorException("Failed to make a md5 hash", e);
    }
  }

  public static String sanitizeFileName(final String fileName) {
    // Use Tika to normalize as a first pass.
    var sanitizedFileName = FilenameUtils.normalize(fileName);

    // Define the regex pattern to match reserved characters
    final var regex = "[<>:\"/\\\\|?*]|" + // file system reserved
        "[\\x00-\\x1F]|" + // control characters
        "[\\x7F\\xA0\\xAD]|" + // non-printing characters DEL, NO-BREAK SPACE, SOFT HYPHEN
        "[#\\[\\]@!$&'()+,;=]|" + // URI reserved
        "[{}^~`´’’'ʼ]"; // URL unsafe characters

    // Remove unauthorized chars
    sanitizedFileName = sanitizedFileName.replaceAll(regex, "-");

    // Keep only authorized ones
    sanitizedFileName = sanitizedFileName.replaceAll("[^a-zA-Z0-9\\.\\-_~]", "-");

    // * Avoids '.' '..' or '.hiddenFiles' .
    sanitizedFileName = sanitizedFileName.replaceFirst("^[\\.\\-]", "");
    sanitizedFileName = FileStorageHelper.beautifyFileName(sanitizedFileName);

    sanitizedFileName =
        Math.abs(UUID.randomUUID().getMostSignificantBits()) + "-" + sanitizedFileName;

    // Maximize filename length to 255 bytes
    final var extension = getFileExtension(sanitizedFileName);
    final var nameWithoutExtension = getFileNameWithoutExtension(sanitizedFileName);

    final var encoding = detectEncoding(nameWithoutExtension);
    final var maxLength = 255 - (extension.isEmpty() ? 0 : extension.length() + 1);

    final var truncatedName = truncateString(nameWithoutExtension, maxLength, encoding);
    return truncatedName + (extension.isEmpty() ? "" : "." + extension);
  }

  private static Charset detectEncoding(final String filename) {
    final var detector = new CharsetDetector();
    detector.setText(filename.getBytes());
    final var charsetMatch = Stream.of(detector.detectAll()).findFirst().orElseThrow();
    return Charset.forName(charsetMatch.getName());
  }

  private static String getFileExtension(final String filename) {
    // Get the file extension
    final var lastDotIndex = filename.lastIndexOf('.');
    return lastDotIndex != -1 && lastDotIndex < filename.length() - 1
        ? filename.substring(lastDotIndex + 1)
        : "";
  }

  private static String getFileNameWithoutExtension(final String filename) {
    // Get the file name without extension
    final var lastDotIndex = filename.lastIndexOf('.');
    return lastDotIndex != -1 ? filename.substring(0, lastDotIndex) : filename;
  }

  private static String truncateString(final String str, final int maxLength,
      final Charset encoding) {
    // Truncate the string to the specified length
    final var bytes = str.getBytes(encoding);
    if (bytes.length <= maxLength) {
      return str;
    }
    return new String(bytes, 0, maxLength, encoding);
  }
}
