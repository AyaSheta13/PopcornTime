# TMDB API Setup Guide

## خطوات إعداد TMDB API

### 1. الحصول على API Key

1. اذهب إلى [TMDB Website](https://www.themoviedb.org/)
2. سجل حساب جديد أو سجل الدخول
3. اذهب إلى [API Settings](https://www.themoviedb.org/settings/api)
4. انقر على "Request API Key"
5. اختر "Developer" كـ نوع الطلب
6. املأ النموذج وانتظر الموافقة (عادة فورية)
7. انسخ API Key الخاص بك

### 2. إضافة API Key إلى المشروع

1. افتح ملف `local.properties` في جذر المشروع
2. أضف السطر التالي:
   ```
   TMDB_API_KEY=your_api_key_here
   ```
3. استبدل `your_api_key_here` بـ API Key الخاص بك

### 3. التحقق من الإعداد

- تأكد من أن `buildConfig = true` موجود في `build.gradle.kts`
- تأكد من أن API key موجود في `local.properties`
- قم بـ Sync للمشروع

### 4. ملاحظات مهمة

- **لا تشارك API key** في الكود أو Git
- ملف `local.properties` موجود في `.gitignore` تلقائياً
- API مجاني ولا يحتاج دفع
- هناك حدود للاستخدام (عادة 40 request كل 10 ثواني)

## البنية الجديدة

### الملفات المُنشأة:

1. **ApiConfig.kt** - إعدادات API
2. **NetworkModule.kt** - إعداد Retrofit
3. **TmdbApiService.kt** - API endpoints
4. **MovieRepository.kt** - Repository layer
5. **MoviesViewModel.kt** - ViewModel للصفحة الرئيسية
6. **MovieDetailsViewModel.kt** - ViewModel لتفاصيل الفيلم
7. **SearchViewModel.kt** - ViewModel للبحث
8. **Result.kt** - Sealed class للـ states
9. **LoadingStates.kt** - UI components للـ loading/error
10. **DTOs** - Data Transfer Objects للـ API responses

## الميزات الجديدة

- ✅ بيانات حقيقية من TMDB API
- ✅ Loading states في جميع الصفحات
- ✅ Error handling كامل
- ✅ Search حقيقي
- ✅ Movie details مع cast و similar movies
- ✅ Genres من API
- ✅ Filtering متقدم

## اختبار التطبيق

بعد إضافة API key:
1. شغل التطبيق
2. ستجد بيانات حقيقية من TMDB
3. جرب البحث عن أفلام
4. افتح تفاصيل أي فيلم لرؤية البيانات الكاملة

