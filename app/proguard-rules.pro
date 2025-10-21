# ---- Jetpack Compose & ViewTree Owners (kritisch gegen deinen NPE) ----
-keep class androidx.compose.ui.platform.AndroidComposeView { *; }
-keep class androidx.lifecycle.ViewTreeLifecycleOwner { *; }
-keep class androidx.lifecycle.ViewTreeViewModelStoreOwner { *; }
-keep class androidx.savedstate.ViewTreeSavedStateRegistryOwner { *; }
-keep class androidx.lifecycle.ReportFragment { *; }

# Activity/Fragment-Basis (damit keine Owner-Bridge wegfällt)
-keep class androidx.activity.ComponentActivity { *; }
-keep class * extends androidx.activity.ComponentActivity
-keep class * extends androidx.fragment.app.Fragment

# (Optional – aber bewährt)
-dontwarn androidx.compose.**
-keep class androidx.savedstate.** { *; }
