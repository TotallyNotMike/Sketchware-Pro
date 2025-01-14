package mod.jbk.code;

import com.besome.sketch.SketchApplication;

import io.github.rosemoe.sora.lang.EmptyLanguage;
import io.github.rosemoe.sora.lang.Language;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.dsl.LanguageDefinitionListBuilder;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;
import kotlin.Unit;
import mod.jbk.util.LogUtil;

public class CodeEditorLanguages {
    private static final String TAG = "CodeEditorLanguages";

    public static final String[] LANGUAGES = {"kotlin.tmLanguage", "xml.tmLanguage.json"};
    public static final String SCOPE_NAME_KOTLIN = "source.kotlin";
    public static final String SCOPE_NAME_XML = "text.xml";

    static {
        FileProviderRegistry.getInstance().addFileProvider(
                new AssetsFileResolver(SketchApplication.getContext().getAssets()));

        for (String language : LANGUAGES) {
            LanguageDefinitionListBuilder builder = new LanguageDefinitionListBuilder();
            String languageName = language.substring(0, language.indexOf('.'));
            builder.language(languageName, languageDefinitionBuilder -> {
                languageDefinitionBuilder.grammar = "textmate/" + language;
                languageDefinitionBuilder.defaultScopeName(language.equals(LANGUAGES[1]) ? "text" : "source");
                return Unit.INSTANCE;
            });

            try {
                GrammarRegistry.getInstance().loadGrammars(builder);
            } catch (Exception e) {
                LogUtil.e(TAG, "Failed to load language '" + language + "'", e);
            }
        }
    }

    public static Language loadTextMateLanguage(String scopeName) {
        Language language;

        try {
            language = TextMateLanguage.create(scopeName, true);
        } catch (Exception | NoSuchMethodError e) {
            LogUtil.e(TAG, "Failed to create Kotlin TextMate language, using empty one as default Kotlin language", e);
            language = new EmptyLanguage();
        }

        return language;
    }
}
