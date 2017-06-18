package net.muse.sound;

/**
 * MIDIイベントを受け取るためのリスナーインタフェースです．
 * <p>
 * MIDIイベントに関連するクラスは、このインタフェースを実装します。さらに、それらのクラスによって作成されたオブジェクトは、コンポーネントの
 * addMIDIEventListener メソッドを使用することによってコンポーネントに登録されます．
 * イベントが発生すると、イベント内容に合わせて，オブジェクトの editConfig メソッドが呼び出されます。
 * 
 * @author Mitsuyo Hashida
 * @since 2007.9.20
 */
public interface MIDIEventListener {
	public void stopPlaying();
	public void stopPlaying(MIDIController synthe);
	public void startPlaying(String smfFilename);
	public void pausePlaying() throws InterruptedException;
}
