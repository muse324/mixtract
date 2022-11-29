package net.muse.misc;

/**
 * <h1>Language</h1>
 * <p>
 * アプリケーションメニューの表示言語を選択します。
 * 
 * @author Mitsuyo Hashida @ Soai University
 * @since 2013/06/12
 */
public class Language {
	public static final Language ENGLISH = new Language(0, "English");
	public static final Language JAPANESE = new Language(1, "Japanese");
	private static Language[] languageList = new Language[] { ENGLISH, JAPANESE };
	private static Language currentLanguage;

	public static final Language create(String val) {
		for (Language l : languageList) {
			if (val.equals(l.getName()))
				return l;
		}
		return ENGLISH; // TODO デフォルトが英語でいいか？
	}

	public static void setCurrentLanguage(Language l) {
		currentLanguage = l;
	}

	public static final Language[] getLanguageList() {
		return languageList;
	}

	private final String name;
	private final int index;

	private Language(int idx, String name) {
		this.index = idx;
		this.name = name;
	}

	/**
	 * @return the index
	 */
	public final int getIndex() {
		return index;
	}

	/**
	 * @return the text
	 */
	public final String getName() {
		return name;
	}

}
