###############################################################
## 1. WICHTIGE EIGENE REGELN (Behebt Ihre Fehler)
###############################################################

# Schützt den öffentlichen, leeren Konstruktor in ALLEN Ihrer Data Classes.
# Dies behebt die "DatabaseException: Class g7.i does not define a no-argument constructor".
-keepclassmembers class ** {
    public <init>();
}

# Schützt die Felder in ALLEN Ihrer Data Classes vor dem Entfernen (Stripping).
# Dies behebt das Problem, dass Firebase-Daten zwar geladen, aber nicht angezeigt werden (fehlende Felder).
-keepclassmembers class ** {
    !static *;
}

###############################################################
## 2. ANDROID/COMPOSE-STABILITÄT (Behebt die NullPointerException)
###############################################################

# Behält alle notwendigen Konstruktoren von Android View-Klassen bei,
# um Reflection-Fehler zu vermeiden (wichtig für AndroidComposeView).
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Behält notwendige Ressourcen-Felder in den generierten R-Klassen bei.
# Dies behebt oft NullPointerException-Fehler bei der Initialisierung von Views/Compose.
-keepclassmembers class **.R$* {
    public static final int *;
}

# Behält kritische Signaturen und Annotationen bei, die von Kotlin/Compose benötigt werden.
-keepattributes Signature
-keepattributes *Annotation*

###############################################################
## 3. ALLGEMEINE SICHERHEIT (Falls nicht automatisch geladen)
###############################################################

# Unterdrückt Warnungen für häufige Abhängigkeiten wie OkHttp und Firebase,
# deren innere Klassen R8 nicht vollständig analysieren kann.
-dontwarn okio.**
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**
-dontwarn org.apache.http.conn.scheme.**
-dontwarn java.nio.file.**

# Behält Methoden bei, die in nativem Code aufgerufen werden (z.B. NDK-Funktionen)
-keepclasseswithmembers class * {
    native <methods>;
}

# Behält Java Serialization bei, falls benötigt (häufig bei älteren Apps/Libs)
-keep class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    private void readObjectNoData();
}