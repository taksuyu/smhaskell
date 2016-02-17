Fujitask
========
- (EN/JP) http://www.slideshare.net/hexx/scalamatsuri-2016-scala
- (JP) http://qiita.com/pab_tech/items/86e4c31d052c678f6fa6
- ScaraMatsuri 2016
	- Dwangoで実際に利用されている
		- (EN) http://dwango.co.jp/english/index.html
- 何を可能にするか？
	- DBとの交信方法(読み/書き)の実装を本体から分離
		- 意味的に同じ交信内容のまま異なるDBを使い分けられる
		- 
	- 交信の目的(create/read/..)、内容(の型)と手段(読み/書き)を静的に定義できる
		- cf. `def create(name: String): Task[ReadWriteTransaction, User]`
	- 
- http://togetter.com/li/909787
	- "トランザクションオブジェクトと権限の型は分けたほうが順当なのではないかと思った"
	- "でないとトランザクションオブジェクトの詳細実装が複数ある場合に困らないかな"
- どう実装されているか？
	- 
- 全モジュールの依存構造を図に起こす
	- 何が抽象化されているか視覚化する
- TaskのPPrint版を実装する
	- 「動く」証拠が必要
- 依存性の問題を解決する
	- Fujitaskライブラリ内の実装を使うなら。
- Futureへの理解が必要
- 同じ目的/機能を達成できるのであれば詳細な実装は参考程度

Presentation
============
- 自分が誰なのか、Scala/Haskellで何をやったか紹介
- Who's done Scala before?
	- Scala言語機能についてどの程度の説明が必要か確認する
- このセッションで何をするか明確にする
	- ある問題に対して、JavaとHaskellを融合させたScalaの実装を、Haskellでも実装できるか試す
		- "Scalaでしか実現できない"などと仰っておられる；実際Haskellで実装できるか気になった
	- 案を出し合う中でお互いのHaskellに関する知識や考え方を共有できればいいと思う
	- ScalaにおけるOOPとFPの融合と応用を知ることでFP全般の更なる理解につながればいいと思う
- 問題を紹介する
	- "Unit of Work"
		- (EN) http://martinfowler.com/eaaCatalog/unitOfWork.html
			- brief explanation of Unit of Work
		- PofEAA (Patterns of Enterprise Application Architecture)
		- http://d.hatena.ne.jp/asakichy/20120720/1342735606
	- (前提)Master/Slave構成のストレージにおいて、ReadWriteはSlaveに問い合わせてはならない
	- Masterに行くかSlaveに行くか個々の場合に記述する必要がある
	- 誤ってMasterに問い合わせる処理(RW)がSlave に行くとき、実行時エラーとなる
	- 誤ってSlave に問い合わせる処理(R) がMasterに行くとき、エラーにならず気づかない(?)
- 問題を克服するFujitaskの機能を紹介する
	- Dwangoで商用利用されていることを明言する
	- 各問い合わせ(Task)について、意味(create/read/..)と結果(引数/戻り値)に使用するトランザクションの種類(R/RW)をタグ付けする
	- 複数のTaskを連結してトランザクションを作成する
		- (Reader)Monadと同じ方法論
			- 最終的にrun()メソッドで実行
	- Task連結の際、最終的に使用されるトランザクションの種類を各Taskのタグの比較から静的に決定する
		- R + R = R
		- RW + RW = RW
		- R + RW = RW
		- RW + R = error
- PPrint版で動作を確認する
- Fujitaskの機能をHaskellで再現ないし拡張する
	- Fujitask: Typeclass, Monad, Subtyping
	- Haskell: ??? (or can Haskell implement subtyping?)
	- 別に解決できなくとも勉強になりさえすれば良いと強調する？

Memo
====
- Illustrator
	- http://blog.ddc.co.jp/mt/dtp/archives/20101122/110544.html
		- round corner for box
	- http://m-school.biz/dev/ai-club/007-line-segment-tool.htm
		- arrow head for line
	- 
