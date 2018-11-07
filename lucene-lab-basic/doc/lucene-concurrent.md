# Lucene 并发

－ 允许任意数量的**只读**IndexReader打开**同一个索引**
- 一个索引只能打开一个IndexWriter, 一旦建立IndexWriter, Lucene会创建一个**文件锁**给它, 关闭时释放.
- IndexReader可以在IndexWriter正在修改索引时打开, 但它只有在IndexWriter提交修改后才能知道索引的修改情况; 
可以在打开IndexReader时传入create=true, 来持续查询索引的情况.
- IndexReader和IndexWriter不仅是线程安全的, 而且是线程友好的, 即呢狗狗很好的扩展到新增线程.

## 远程访问

远程访问索引的效果在比本地访问差很多.

最好是将索引**复制**到各个机器上, 然后在各个机器的本地进行访问, 最后通过一些**复制策略来同步数据**, 例如Solr.

远程访问的问题:

- 不连贯的客户端缓存, 即当一个IndexWriter提交后, 同时另一个IndexReader或IndexWriter重新打开索引, 
则有可能会抛出FileNotFoundException. 解决方案是**重新操作**, 因为客户端缓存会在出现问题一段时间后自动修复.
- 如何删除一个被其他机器打开的索引, 这种情况在NFS机器上尤为明显, 
因为大多Unix本地文件系统在删除时会保留该文件的磁盘分配空间, 直到所有文件句柄关闭才释放(该策略称为**最后关闭删除(delete on last close)**).
而NFS却不是这样的, 它只是简单的删除文件, 导致拥有文件句柄的其他机器会面临**过时的NFS文件句柄**IOException异常.


