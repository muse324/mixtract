package net.muse.mixtract.command;

import net.muse.data.Group;
import net.muse.data.GroupType;
import net.muse.misc.MuseObject;
import net.muse.mixtract.data.*;

/**
 * 入力されたMusicXMLに対し，グループと頂点を推定します．
 *
 * @Input MusicXML ファイル（単旋律・複数旋律どちらでも）
 * @Output none
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         <address>http://www.m-use.net/</address>
 * @since 2008/04/05
 */
public class GroupAnalyzer extends MuseObject implements Runnable {

	public static final int rootDiv = 480;
	private final MXTuneData data;
	private Group root;

	/**
	 * @param doScoreAnalysis セットする doScoreAnalysis
	 */
	private static final void setScoreAnalysis(boolean b) {}

	/**
	 * @param target グループ構造を分析するTuneData
	 * @param doScoreAnalysis インスタンス化と同時に楽譜分析をするかどうか
	 */
	public GroupAnalyzer(MXTuneData target, boolean doScoreAnalysis) {
		setScoreAnalysis(doScoreAnalysis);
		data = target;
	}

	/**
	 * @param target
	 */
	public void setRootGroup(Group target) {
		root = target;
	}

	private PrimaryPhraseSequence _pendingSequence = null;

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if (root == null) {
			_pendingSequence = null;
			_completeHierarcy = false;
			createUpperLevelStructure(data.getGroupSequence().root());
			return;
		}
	}

	/**
	 * @param sequence
	 */
	private void createUpperLevelStructure(PrimaryPhraseSequence sequence) {
		if (sequence == null || !sequence.hasNext())
			return;
		Group g1 = sequence.getGroup();
		Group g2 = sequence.next().getGroup();
		// g1とg2の長さがほぼ等価（GPR5:symmetry）なら親グループを生成
		if (symmetryRate(g1.getTimeValue(), g2.getTimeValue()) <= 0.3) {
			Group parent = new Group(new GroupNote(g1.getBeginGroupNote().getNote()),
					new GroupNote(g2.getEndGroupNote().getNote()), GroupType.PARENT);
			parent.setIndex(data.getUniqueGroupIndex());
			_completeHierarcy = false;
			parent = reachedHierarchy(parent, data.getRootGroup(0));
			parent.setChild(g1, g2);
			// 非階層グループリストからg1, g2を削除
			if (data.getGroupArrayList().contains(g1))
				data.getGroupArrayList().remove(g1);
			if (data.getGroupArrayList().contains(g2))
				data.getGroupArrayList().remove(g2);
			if (_completeHierarcy) // 終了
				return;

			PrimaryPhraseSequence parentseq = new PrimaryPhraseSequence(parent);
			parentseq.setNext(sequence.next().next());
			if (parentseq.hasNext())
				createUpperLevelStructure(parentseq);
			else {
				// 曲の最後まで探索したのでペンディングを再度探索
				addPendingSequence(parentseq);
				data.addGroupArrayList(parent);
				createUpperLevelStructure(_pendingSequence.root());
			}
		} else {
			// sequence.setNext(null);
			addPendingSequence(sequence);
			data.addGroupArrayList(g1);
			createUpperLevelStructure(sequence.next());
		}
	}

	/**
	 * @param target
	 * @param rootGroup
	 * @return
	 */
	private Group reachedHierarchy(Group target, Group rootGroup) {
		if (rootGroup == null)
			return target;
		if (_completeHierarcy)
			return target;
		target = reachedHierarchy(target, rootGroup.getChildFormerGroup());
		target = reachedHierarchy(target, rootGroup.getChildLatterGroup());
		if (target.nearlyEquals(rootGroup)) {
			_completeHierarcy = true;
			if (!rootGroup.hasParent())
				return rootGroup;
			Group parent = rootGroup.getParent();
			if (parent.getChildFormerGroup().equals(rootGroup))
				parent.setChild(target, parent.getChildLatterGroup());
			else if (parent.getChildLatterGroup().equals(rootGroup))
				parent.setChild(parent.getChildFormerGroup(), target);
			target.setIndex(rootGroup.index());
			return target;
		}
		return target;
	}

	/**
	 * @param sequence
	 */
	private void addPendingSequence(PrimaryPhraseSequence sequence) {
		if (_pendingSequence == null)
			_pendingSequence = sequence;
		else if (_pendingSequence.getGroup().getEndGroupNote().getNote().next()
				.equals(sequence.getGroup().getBeginGroupNote().getNote())) {
			_pendingSequence.setNext(sequence);
		}
	}

	/**
	 * @param timeValue
	 * @param timeValue2
	 * @return
	 */
	private double symmetryRate(double val1, double val2) {
		double rate = (val1 <= val2) ? val1 / val2 : val2 / val1;
		return 1.0 - rate;
	}

	private boolean _completeHierarcy;
}
