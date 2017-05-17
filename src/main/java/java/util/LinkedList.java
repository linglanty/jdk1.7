/*
 * Copyright (c) 1997, 2011, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java.util;

/**
 * Doubly-linked list implementation of the {@code List} and {@code Deque}
 * interfaces.  Implements all optional list operations, and permits all
 * elements (including {@code null}).
 *
 * <p>All of the operations perform as could be expected for a doubly-linked
 * list.  Operations that index into the list will traverse the list from
 * the beginning or the end, whichever is closer to the specified index.
 *
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a linked list concurrently, and at least
 * one of the threads modifies the list structurally, it <i>must</i> be
 * synchronized externally.  (A structural modification is any operation
 * that adds or deletes one or more elements; merely setting the value of
 * an element is not a structural modification.)  This is typically
 * accomplished by synchronizing on some object that naturally
 * encapsulates the list.
 *
 * If no such object exists, the list should be "wrapped" using the
 * {@link Collections#synchronizedList Collections.synchronizedList}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the list:<pre>
 *   List list = Collections.synchronizedList(new LinkedList(...));</pre>
 *
 * <p>The iterators returned by this class's {@code iterator} and
 * {@code listIterator} methods are <i>fail-fast</i>: if the list is
 * structurally modified at any time after the iterator is created, in
 * any way except through the Iterator's own {@code remove} or
 * {@code add} methods, the iterator will throw a {@link
 * java.util.ConcurrentModificationException}.  Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than
 * risking arbitrary, non-deterministic behavior at an undetermined
 * time in the future.
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw {@code ConcurrentModificationException} on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness:   <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @author  Josh Bloch
 * @see     java.util.List
 * @see     java.util.ArrayList
 * @since 1.2
 * @param <E> the type of elements held in this collection
 */

public class LinkedList<E>
    extends java.util.AbstractSequentialList<E>
    implements java.util.List<E>, java.util.Deque<E>, Cloneable, java.io.Serializable
{
    transient int size = 0;

    //java语言的关键字，变量修饰符，如果用transient声明一个实例变量,该变量不参与序列化过程。
    //but 啥是序列化过程。。。

    /**
     * Pointer to first node.
     * Invariant: (first == null && last == null) ||
     *            (first.prev == null && first.item != null)
     */
    transient Node<E> first;

    /**
     * Pointer to last node.
     * Invariant: (first == null && last == null) ||
     *            (last.next == null && last.item != null)
     */
    transient Node<E> last;

    /**
     * Constructs an empty list.
     */
    public LinkedList() {
    }
    //一个空的构造函数

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param  c the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
    public LinkedList(java.util.Collection<? extends E> c) {
        this();
        addAll(c);
    }
    //this()转向空构造函数，addAll()将参数中的collection c中的元素加入末尾
    //构造一个linklist，其中的元素是来自于collection中的元素

    /**
     * Links e as first element.
     */
    private void linkFirst(E e) {
        final Node<E> f = first;
        final Node<E> newNode = new Node<>(null, e, f);
        first = newNode;
        if (f == null)
            last = newNode;
        else
            f.prev = newNode;
        size++;
        modCount++;
    }
    //创建newNode的时候，已经指向next为原先的first，所以当原先list不空时，需要令原先first的prev指向newNode

    /**
     * Links e as last element.
     */
    void linkLast(E e) {
        final Node<E> l = last;
        final Node<E> newNode = new Node<>(l, e, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
        size++;
        modCount++;
    }

    /**
     * Inserts element e before non-null Node succ.
     */
    void linkBefore(E e, Node<E> succ) {
        // assert succ != null;
        final Node<E> pred = succ.prev;
        final Node<E> newNode = new Node<>(pred, e, succ);
        succ.prev = newNode;
        if (pred == null)
            first = newNode;
        else
            pred.next = newNode;
        size++;
        modCount++;
    }
    //在结点succ前插入一个元素e，e的前驱是succ的前驱，如果succ的前驱为空，则newNode是list的头结点，否则，newNode是succ前驱的后继

    /**
     * Unlinks non-null first node f.
     */
    private E unlinkFirst(Node<E> f) {
        // assert f == first && f != null;
        final E element = f.item;
        final Node<E> next = f.next;
        f.item = null;
        f.next = null; // help GC
        first = next;
        if (next == null)
            last = null;
        else
            next.prev = null;
        size--;
        modCount++;
        return element;
    }
    //假设f是一个linklist的第一个且不空的结点，该函数的目的是删去这个结点，且返回该结点的值。
    //首先用element和next分别保存f的值和下个结点，然后置f为空，并且断开f与list，next置为头结点，且处理删除f后的list。

    /**
     * Unlinks non-null last node l.
     */
    private E unlinkLast(Node<E> l) {
        // assert l == last && l != null;
        final E element = l.item;
        final Node<E> prev = l.prev;
        l.item = null;
        l.prev = null; // help GC
        last = prev;
        if (prev == null)
            first = null;
        else
            prev.next = null;
        size--;
        modCount++;
        return element;
    }
    //同上，删除linklist中最后一个且非空的结点，返回它的值并做相应的链表处理。

    /**
     * Unlinks non-null node x.
     */
    E unlink(Node<E> x) {
        // assert x != null;
        final E element = x.item;
        final Node<E> next = x.next;
        final Node<E> prev = x.prev;

        if (prev == null) {
            //prev == null 说明当前结点x是头结点，删去后，当前结点的下一个结点是first
            first = next;
        } else {
            //当前结点不是头结点，当前结点的前驱的后继指向当前结点的后继
            prev.next = next;
            x.prev = null;
            //断开链接
        }

        if (next == null) {
            //当前结点x是尾结点，删去后当前结点的前驱是尾结点
            last = prev;
        } else {
            //当前结点x不是尾结点，当前结点的前驱是当前结点后继的前驱
            next.prev = prev;
            x.next = null;
            //断开链接
        }
        //删去链表中的一个非空元素

        x.item = null;
        size--;
        modCount++;
        return element;
    }

    /**
     * Returns the first element in this list.
     *
     * @return the first element in this list
     * @throws java.util.NoSuchElementException if this list is empty
     */
    public E getFirst() {
        final Node<E> f = first;
        if (f == null)
            throw new java.util.NoSuchElementException();
        return f.item;
    }
    //获取linklist的头元素

    /**
     * Returns the last element in this list.
     *
     * @return the last element in this list
     * @throws java.util.NoSuchElementException if this list is empty
     */
    public E getLast() {
        final Node<E> l = last;
        if (l == null)
            throw new java.util.NoSuchElementException();
        return l.item;
    }
    //获取linklist的尾元素

    /**
     * Removes and returns the first element from this list.
     *
     * @return the first element from this list
     * @throws java.util.NoSuchElementException if this list is empty
     */
    public E removeFirst() {
        final Node<E> f = first;
        if (f == null)
            throw new java.util.NoSuchElementException();
        return unlinkFirst(f);
    }
    //调用解除linklist的一个结点的方法，只是没有显式的给出这个结点

    /**
     * Removes and returns the last element from this list.
     *
     * @return the last element from this list
     * @throws java.util.NoSuchElementException if this list is empty
     */
    public E removeLast() {
        final Node<E> l = last;
        if (l == null)
            throw new java.util.NoSuchElementException();
        return unlinkLast(l);
    }
    //同上

    /**
     * Inserts the specified element at the beginning of this list.
     *
     * @param e the element to add
     */
    public void addFirst(E e) {
        linkFirst(e);
    }
    //在链头添加一个元素

    /**
     * Appends the specified element to the end of this list.
     *
     * <p>This method is equivalent to {@link #add}.
     *
     * @param e the element to add
     */
    public void addLast(E e) {
        linkLast(e);
    }
    //在链尾添加一个元素

    /**
     * Returns {@code true} if this list contains the specified element.
     * More formally, returns {@code true} if and only if this list contains
     * at least one element {@code e} such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
     *
     * @param o element whose presence in this list is to be tested
     * @return {@code true} if this list contains the specified element
     */
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }
    //查找某个元素是否在linklist中，就是找他的下标是否存在，若不存在，则返回false。

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    public int size() {
        return size;
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * <p>This method is equivalent to {@link #addLast}.
     *
     * @param e element to be appended to this list
     * @return {@code true} (as specified by {@link java.util.Collection#add})
     */
    public boolean add(E e) {
        linkLast(e);
        return true;
    }
    //在linklist尾部添加一个元素，并且返回true

    /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present.  If this list does not contain the element, it is
     * unchanged.  More formally, removes the element with the lowest index
     * {@code i} such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
     * (if such an element exists).  Returns {@code true} if this list
     * contained the specified element (or equivalently, if this list
     * changed as a result of the call).
     *
     * @param o element to be removed from this list, if present
     * @return {@code true} if this list contained the specified element
     */
    public boolean remove(Object o) {
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }
    //删除linklist中值为o的结点，从前向后遍历linklist，如果遇到跟o相等的值，就删除这个结点

    /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the specified
     * collection's iterator.  The behavior of this operation is undefined if
     * the specified collection is modified while the operation is in
     * progress.  (Note that this will occur if the specified collection is
     * this list, and it's nonempty.)
     *
     * @param c collection containing elements to be added to this list
     * @return {@code true} if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    public boolean addAll(java.util.Collection<? extends E> c) {
        return addAll(size, c);
    }
    //从当前的size位置（末尾）加入c中的所有元素

    /**
     * Inserts all of the elements in the specified collection into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the order that they are returned by the
     * specified collection's iterator.
     *
     * @param index index at which to insert the first element
     *              from the specified collection
     * @param c collection containing elements to be added to this list
     * @return {@code true} if this list changed as a result of the call
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws NullPointerException if the specified collection is null
     */
    public boolean addAll(int index, java.util.Collection<? extends E> c) {
        //从index位置（包括index位置）开始加入c中的元素，并保留index及其之后的元素
        checkPositionIndex(index);
        //检查index位置是否在[0，size]之间

        Object[] a = c.toArray();
        int numNew = a.length;
        if (numNew == 0)
            return false;

        //用a 以及numNew分别保存c中的元素和数目
        Node<E> pred, succ;
        if (index == size) {
            //插入位置恰好在原list的末尾
            succ = null;
            pred = last;
        } else {
            //插入位置在[0,size]之间
            succ = node(index);
            pred = succ.prev;
        }
        //succ保存的是当前index位置处的元素，用于插入c的元素后将index及后续元素链接到c的后面
        //pred保存插入位置的前一个元素

        for (Object o : a) {
            @SuppressWarnings("unchecked") E e = (E) o;
            Node<E> newNode = new Node<>(pred, e, null);
            if (pred == null)
                first = newNode;
            else
                pred.next = newNode;
            pred = newNode;
            //向后移动一个结点
        }
        //遍历c，将c中的元素依次插入list，每次都生成一个newNode，前驱是当前的pred，pred的后继是newNode

        if (succ == null) {
            last = pred;
        } else {
            pred.next = succ;
            succ.prev = pred;
        }
        //c中元素都加入list后，原来index位置的结点及其后续succ连接到当前的list的末尾pred

        size += numNew;
        //list的长度发生变化
        modCount++;
        return true;
    }

    /**
     * Removes all of the elements from this list.
     * The list will be empty after this call returns.
     */
    public void clear() {
        // Clearing all of the links between nodes is "unnecessary", but:
        // - helps a generational GC if the discarded nodes inhabit
        //   more than one generation
        // - is sure to free memory even if there is a reachable Iterator
        for (Node<E> x = first; x != null; ) {
            Node<E> next = x.next;
            x.item = null;
            x.next = null;
            x.prev = null;
            x = next;
        }
        first = last = null;
        size = 0;
        modCount++;
    }


    // Positional Access Operations

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E get(int index) {
        checkElementIndex(index);
        return node(index).item;
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E set(int index, E element) {
        checkElementIndex(index);
        Node<E> x = node(index);
        E oldVal = x.item;
        x.item = element;
        return oldVal;
    }

    /**
     * Inserts the specified element at the specified position in this list.
     * Shifts the element currently at that position (if any) and any
     * subsequent elements to the right (adds one to their indices).
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public void add(int index, E element) {
        checkPositionIndex(index);

        if (index == size)
            linkLast(element);
        else
            linkBefore(element, node(index));
    }

    /**
     * Removes the element at the specified position in this list.  Shifts any
     * subsequent elements to the left (subtracts one from their indices).
     * Returns the element that was removed from the list.
     *
     * @param index the index of the element to be removed
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E remove(int index) {
        checkElementIndex(index);
        return unlink(node(index));
    }

    /**
     * Tells if the argument is the index of an existing element.
     */
    private boolean isElementIndex(int index) {
        return index >= 0 && index < size;
    }

    /**
     * Tells if the argument is the index of a valid position for an
     * iterator or an add operation.
     */
    private boolean isPositionIndex(int index) {
        return index >= 0 && index <= size;
    }

    /**
     * Constructs an IndexOutOfBoundsException detail message.
     * Of the many possible refactorings of the error handling code,
     * this "outlining" performs best with both server and client VMs.
     */
    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }

    private void checkElementIndex(int index) {
        if (!isElementIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private void checkPositionIndex(int index) {
        if (!isPositionIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * Returns the (non-null) Node at the specified element index.
     */
    Node<E> node(int index) {
        // assert isElementIndex(index);
        // 返回list中index位置上的Node，如果index在前半部分，则从前往后找，否则从后往前找

        if (index < (size >> 1)) {
            Node<E> x = first;
            for (int i = 0; i < index; i++)
                x = x.next;
            return x;
        } else {
            Node<E> x = last;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }

    // Search Operations

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index {@code i} such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     *
     * @param o element to search for
     * @return the index of the first occurrence of the specified element in
     *         this list, or -1 if this list does not contain the element
     */
    public int indexOf(Object o) {
        int index = 0;
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == null)
                    return index;
                index++;
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.item))

                    //o的this是否等于x.item

                    return index;
                index++;
            }
        }
        return -1;
    }
    //找到object o的索引下标，注意，这里node的数据域定义的是继承自object的类，所以，o分为null与非null两种，各自都是从头开始遍历list，返回值等于o的下标

    /**
     * Returns the index of the last occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the highest index {@code i} such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     *
     * @param o element to search for
     * @return the index of the last occurrence of the specified element in
     *         this list, or -1 if this list does not contain the element
     */
    public int lastIndexOf(Object o) {
        int index = size;
        if (o == null) {
            for (Node<E> x = last; x != null; x = x.prev) {
                index--;
                if (x.item == null)
                    return index;
            }
        } else {
            for (Node<E> x = last; x != null; x = x.prev) {
                index--;
                if (o.equals(x.item))
                    return index;
            }
        }
        return -1;
    }
    //从后往前遍历linklist，从中找到o的索引下标

    // Queue operations.

    /**
     * Retrieves, but does not remove, the head (first element) of this list.
     *
     * @return the head of this list, or {@code null} if this list is empty
     * @since 1.5
     */
    public E peek() {
        final Node<E> f = first;
        return (f == null) ? null : f.item;
    }
    //获取访问linklist中的头结点元素，但是不移除，如果头结点为空，则返回null

    /**
     * Retrieves, but does not remove, the head (first element) of this list.
     *
     * @return the head of this list
     * @throws java.util.NoSuchElementException if this list is empty
     * @since 1.5
     */
    public E element() {
        return getFirst();
    }
    // 获取访问linklist中的头结点元素，但是不移除，如果头结点为空，则抛出异常

    /**
     * Retrieves and removes the head (first element) of this list.
     *
     * @return the head of this list, or {@code null} if this list is empty
     * @since 1.5
     */
    public E poll() {
        final Node<E> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }
    //摘掉并返回linklist的头结点，如果该头结点为null，则返回null

    /**
     * Retrieves and removes the head (first element) of this list.
     *
     * @return the head of this list
     * @throws java.util.NoSuchElementException if this list is empty
     * @since 1.5
     */
    public E remove() {
        return removeFirst();
    }
    //摘掉并返回linklist的头结点，如果头结点为null，则抛出异常



    /**
     * Adds the specified element as the tail (last element) of this list.
     *
     * @param e the element to add
     * @return {@code true} (as specified by {@link java.util.Queue#offer})
     * @since 1.5
     */

    public boolean offer(E e) {
        return add(e);
    }
    //本质还是linklast（e）

    // Deque operations
    /**
     * Inserts the specified element at the front of this list.
     *
     * @param e the element to insert
     * @return {@code true} (as specified by {@link java.util.Deque#offerFirst})
     * @since 1.6
     */
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }
    //本质还是linkfirst（e）

    /**
     * Inserts the specified element at the end of this list.
     *
     * @param e the element to insert
     * @return {@code true} (as specified by {@link java.util.Deque#offerLast})
     * @since 1.6
     */
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }
    //本质还是linklast（e）

    /**
     * offerLast（），offer（）都是调用linklast（）方法，在linklast尾部添加元素；
     * offerFirst（）是调用linkfirst（）方法，在linkfirst处添加元素
     */

    /**
     * Retrieves, but does not remove, the first element of this list,
     * or returns {@code null} if this list is empty.
     *
     * @return the first element of this list, or {@code null}
     *         if this list is empty
     * @since 1.6
     */
    public E peekFirst() {
        final Node<E> f = first;
        return (f == null) ? null : f.item;
     }

    /**
     * Retrieves, but does not remove, the last element of this list,
     * or returns {@code null} if this list is empty.
     *
     * @return the last element of this list, or {@code null}
     *         if this list is empty
     * @since 1.6
     */
    public E peekLast() {
        final Node<E> l = last;
        return (l == null) ? null : l.item;
    }

    /**
     * Retrieves and removes the first element of this list,
     * or returns {@code null} if this list is empty.
     *
     * @return the first element of this list, or {@code null} if
     *     this list is empty
     * @since 1.6
     */
    public E pollFirst() {
        final Node<E> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }

    /**
     * Retrieves and removes the last element of this list,
     * or returns {@code null} if this list is empty.
     *
     * @return the last element of this list, or {@code null} if
     *     this list is empty
     * @since 1.6
     */
    public E pollLast() {
        final Node<E> l = last;
        return (l == null) ? null : unlinkLast(l);
    }

    /**
     * 有关获取头、尾结点元素的方法总结：
     * peek（），peekFirst（）获取头结点但不删除，如果头结点为空，则返回空；
     * element（）获取头结点但不删除，如果头结点为空，则抛出没有这样的元素异常；
     * poll（），pollFirst（）获取并删除头结点，如果头结点为空，则返回空；
     * remove（）获取并删除头结点，如果头结点为空，则抛出没有这样的元素异常。
     * 有关获取尾结点的方法总结：
     * peekLast（）获取尾结点但不删除，如果尾结点为空，则返回空；
     * pollLast（）获取尾结点并并删除该结点，如果尾结点为空，则返回空。
     */


    /**
     * Pushes an element onto the stack represented by this list.  In other
     * words, inserts the element at the front of this list.
     *
     * <p>This method is equivalent to {@link #addFirst}.
     *
     * @param e the element to push
     * @since 1.6
     */
    public void push(E e) {
        addFirst(e);
    }
    //将 一个元素加入链表头部，相当于入栈

    /**
     * Pops an element from the stack represented by this list.  In other
     * words, removes and returns the first element of this list.
     *
     * <p>This method is equivalent to {@link #removeFirst()}.
     *
     * @return the element at the front of this list (which is the top
     *         of the stack represented by this list)
     * @throws java.util.NoSuchElementException if this list is empty
     * @since 1.6
     */
    public E pop() {
        return removeFirst();
    }
    //将一个头结点元素（栈顶元素）移除并返回

    /**
     * Removes the first occurrence of the specified element in this
     * list (when traversing the list from head to tail).  If the list
     * does not contain the element, it is unchanged.
     *
     * @param o element to be removed from this list, if present
     * @return {@code true} if the list contained the specified element
     * @since 1.6
     */
    public boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }

    //删除linklist中第一个等于o的结点
    /**
     * Removes the last occurrence of the specified element in this
     * list (when traversing the list from head to tail).  If the list
     * does not contain the element, it is unchanged.
     *
     * @param o element to be removed from this list, if present
     * @return {@code true} if the list contained the specified element
     * @since 1.6
     */
    public boolean removeLastOccurrence(Object o) {
        if (o == null) {
            for (Node<E> x = last; x != null; x = x.prev) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<E> x = last; x != null; x = x.prev) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }
    // 从linkllist的尾部开始遍历，找到最后一次出现o的结点，删除并返回false布尔值

    /**
     * Returns a list-iterator of the elements in this list (in proper
     * sequence), starting at the specified position in the list.
     * Obeys the general contract of {@code List.listIterator(int)}.<p>
     *
     * The list-iterator is <i>fail-fast</i>: if the list is structurally
     * modified at any time after the Iterator is created, in any way except
     * through the list-iterator's own {@code remove} or {@code add}
     * methods, the list-iterator will throw a
     * {@code ConcurrentModificationException}.  Thus, in the face of
     * concurrent modification, the iterator fails quickly and cleanly, rather
     * than risking arbitrary, non-deterministic behavior at an undetermined
     * time in the future.
     *
     * @param index index of the first element to be returned from the
     *              list-iterator (by a call to {@code next})
     * @return a ListIterator of the elements in this list (in proper
     *         sequence), starting at the specified position in the list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @see java.util.List#listIterator(int)
     */
    public java.util.ListIterator<E> listIterator(int index) {
        checkPositionIndex(index);
        return new ListItr(index);
    }
    //首先检查传入的index参数的合法性，然后将index位置的结点赋给next

    private class ListItr implements java.util.ListIterator<E> {

        /**
         * Iterator 是一个java迭代器，迭代器是一种设计模式，是一个对象，可以用来遍历并选择序列中的对象，而不需要关注底层的细节
         * 只能单向移动，继承自他的listIterator不仅可两个方向移动还可以插入删除元素。
         */
        //listItr是对继承自Iterator接口的ListIterator接口的实现
        private Node<E> lastReturned = null;
        //用于记录上一次迭代的结点
        private Node<E> next;
        //用于记录下一个要遍历的位置
        private int nextIndex;
        //记录下一个要遍历的位置的索引
        private int expectedModCount = modCount;

        ListItr(int index) {
            //listItr的带参构造函数，表示遍历从index开始
            // assert isPositionIndex(index);
            next = (index == size) ? null : node(index);
            nextIndex = index;
        }
        //如果当前的index已经到达末尾，则记录list的next为空，否则设置index位置处的结点为下即将一个遍历的结点，nextindex为index

        public boolean hasNext() {
            return nextIndex < size;
        }
        //判断是否有后继

        public E next() {
            checkForComodification();
            if (!hasNext())
                throw new java.util.NoSuchElementException();
            //已到达linklist末尾

            lastReturned = next;
            //下一个即将遍历的结点（被返回）存储在lastReturned中
            next = next.next;//遍历向右移动一位
            nextIndex++;
            return lastReturned.item;
        }
        //list中还有元素，返回下一个元素

        public boolean hasPrevious() {
            return nextIndex > 0;
        }

        public E previous() {
            checkForComodification();
            if (!hasPrevious())
                throw new java.util.NoSuchElementException();

            next = (next == null) ? last : next.prev;
            //如果当前已经便利到list末尾，那么他的前一个元素（即将遍历的）是last，否则是当前元素next的prev
            lastReturned = next;;
            nextIndex--;
            return lastReturned.item;
        }
        /***
         * 注意区别next（）和previous（）：
         * next（）中，由于next指针指向向右方向下一个node，所以向右遍历时，先访问这个结点在移动指针；
         * previous（）中，同样由于next指针指向向右方向的下一个node，所以向左遍历时，先修改指针，在访问指针指向的结点
         */

        public int nextIndex() {
            return nextIndex;
        }

        public int previousIndex() {
            return nextIndex - 1;
        }

        public void remove() {
            // remove（）方法只能用在next（）或previous（）方法后，add（）方法后不可以使用，但是remove（）后可以add（）
            checkForComodification();
            if (lastReturned == null)
                throw new IllegalStateException();

            Node<E> lastNext = lastReturned.next;
            //lastNext用于保存被移除的node的next
            unlink(lastReturned);
            //从list中移除lastReturned，并将该结点前后结点链接起来
            if (next == lastReturned)
                //对应于previous（）情况，由于先移动指针后获取前继结点，所以next== lastReturned，
                // remove了lastReturned结点后，next指针丢失，所以将lastNext赋给next，
                //另外由于新的next代替原来的next，所以nextIndex不变
                next = lastNext;
            else
                //对应于next（）情况，由于先获取后继结点后移动指针，所以next！=lastReturned，remove了lastReturned后，
                // next保持原来的不发生变化，但是其对应的nextIndex减1
                nextIndex--;
            lastReturned = null;
            expectedModCount++;
        }

        public void set(E e) {
            if (lastReturned == null)
                throw new IllegalStateException();
            checkForComodification();
            lastReturned.item = e;
        }
        //修改当前遍历的结点的值

        public void add(E e) {
            checkForComodification();
            lastReturned = null;
            if (next == null)
                linkLast(e);
            //当前迭代器已经遍历到list末尾，则直接添加在list末尾
            else
                linkBefore(e, next);
            //当前迭代器没有遍历到list末尾，则在下一个遍历的结点前加入
            nextIndex++;
            expectedModCount++;
        }

        final void checkForComodification() {
            //网上说是fail-fast，并不懂
            if (modCount != expectedModCount)
                throw new java.util.ConcurrentModificationException();
        }
    }

    private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
        //list是链表，其中结点Node的定义如上，数据＋双向指针。list底层是一个双向链表
    }

    /**
     * @since 1.6
     */
    public java.util.Iterator<E> descendingIterator() {
        return new DescendingIterator();
    }

    /**
     * Adapter to provide descending iterators via ListItr.previous
     */
    private class DescendingIterator implements java.util.Iterator<E> {
        private final ListItr itr = new ListItr(size());
        public boolean hasNext() {
            return itr.hasPrevious();
        }
        public E next() {
            return itr.previous();
        }
        public void remove() {
            itr.remove();
        }
    }
    //在降序的迭代器中，hasnext（）和hasprevious（）方法与升序中的相反。

    @SuppressWarnings("unchecked")
    private LinkedList<E> superClone() {
        try {
            return (LinkedList<E>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    /**
     * Returns a shallow copy of this {@code LinkedList}. (The elements
     * themselves are not cloned.)
     *
     * @return a shallow copy of this {@code LinkedList} instance
     */
    public Object clone() {
        LinkedList<E> clone = superClone();

        // Put clone into "virgin" state
        clone.first = clone.last = null;
        clone.size = 0;
        clone.modCount = 0;

        // Initialize clone with our elements
        for (Node<E> x = first; x != null; x = x.next)
            clone.add(x.item);

        return clone;
    }

    /**
     * Returns an array containing all of the elements in this list
     * in proper sequence (from first to last element).
     *
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this list.  (In other words, this method must allocate
     * a new array).  The caller is thus free to modify the returned array.
     *
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all of the elements in this list
     *         in proper sequence
     */
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (Node<E> x = first; x != null; x = x.next)
            result[i++] = x.item;
        return result;
    }

    /**
     * Returns an array containing all of the elements in this list in
     * proper sequence (from first to last element); the runtime type of
     * the returned array is that of the specified array.  If the list fits
     * in the specified array, it is returned therein.  Otherwise, a new
     * array is allocated with the runtime type of the specified array and
     * the size of this list.
     *
     * <p>If the list fits in the specified array with room to spare (i.e.,
     * the array has more elements than the list), the element in the array
     * immediately following the end of the list is set to {@code null}.
     * (This is useful in determining the length of the list <i>only</i> if
     * the caller knows that the list does not contain any null elements.)
     *
     * <p>Like the {@link #toArray()} method, this method acts as bridge between
     * array-based and collection-based APIs.  Further, this method allows
     * precise control over the runtime type of the output array, and may,
     * under certain circumstances, be used to save allocation costs.
     *
     * <p>Suppose {@code x} is a list known to contain only strings.
     * The following code can be used to dump the list into a newly
     * allocated array of {@code String}:
     *
     * <pre>
     *     String[] y = x.toArray(new String[0]);</pre>
     *
     * Note that {@code toArray(new Object[0])} is identical in function to
     * {@code toArray()}.
     *
     * @param a the array into which the elements of the list are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose.
     * @return an array containing the elements of the list
     * @throws ArrayStoreException if the runtime type of the specified array
     *         is not a supertype of the runtime type of every element in
     *         this list
     * @throws NullPointerException if the specified array is null
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size)
            a = (T[])java.lang.reflect.Array.newInstance(
                                a.getClass().getComponentType(), size);
        int i = 0;
        Object[] result = a;
        for (Node<E> x = first; x != null; x = x.next)
            result[i++] = x.item;

        if (a.length > size)
            a[size] = null;

        return a;
    }
    //linklist中的所有元素存入数组a，并返回

    private static final long serialVersionUID = 876323262645176354L;

    /**
     * Saves the state of this {@code LinkedList} instance to a stream
     * (that is, serializes it).
     *
     * @serialData The size of the list (the number of elements it
     *             contains) is emitted (int), followed by all of its
     *             elements (each an Object) in the proper order.
     */
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException {
        // Write out any hidden serialization magic
        s.defaultWriteObject();

        // Write out size
        s.writeInt(size);

        // Write out all elements in the proper order.
        for (Node<E> x = first; x != null; x = x.next)
            s.writeObject(x.item);
    }

    /**
     * Reconstitutes this {@code LinkedList} instance from a stream
     * (that is, deserializes it).
     */
    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        // Read in any hidden serialization magic
        s.defaultReadObject();

        // Read in size
        int size = s.readInt();

        // Read in all elements in the proper order.
        for (int i = 0; i < size; i++)
            linkLast((E)s.readObject());
    }
}
