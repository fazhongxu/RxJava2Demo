package com.xxl.rxjava2demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "RxJava2Demo";
    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        initData0();
//        initData1();
//        initData2();
//        initData3();
//        initData4();
//        initData5();
//        initData6();
//        initData7();
//        initData8();
//        initData9();
        initData();
    }

    private void initData() {//Flowable Subscriber
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Integer> emitter) throws Exception {
                Log.e(TAG, "subscribe: " + emitter.requested());//获取下游请求事件数量
                Log.e(TAG, "emitter: " + 1);
                emitter.onNext(1);
                Log.e(TAG, "subscribe: " + emitter.requested());
                Log.e(TAG, "emitter: " + 2);
                emitter.onNext(2);
                Log.e(TAG, "subscribe: " + emitter.requested());
                Log.e(TAG, "emitter: " + 3);
                emitter.onNext(3);
                Log.e(TAG, "subscribe: " + emitter.requested());
                emitter.onComplete();
                Log.e(TAG, "subscribe: "+4);
                emitter.onNext(4);
                Log.e(TAG, "subscribe: " + emitter.requested());
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(1000);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.e(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: ");
                    }
                });


    }

    private void initData9() {//Flowable Subscriber方式使用
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Integer> emitter) throws Exception {
                Log.e(TAG, "subscribe: " + 1);
                emitter.onNext(1);
                Log.e(TAG, "subscribe: " + 2);
                emitter.onNext(2);
                emitter.onComplete();
                Log.e(TAG, "subscribe: " + 3);
                emitter.onNext(3);
            }
        }, BackpressureStrategy.ERROR).subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);//只有调用了subscription.request方法，下游才能接收到事件，不调用此方法会发生MissingBackpressureException,在onError里面抛异常
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "onNext: " + integer);
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "onError: " + t);
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete: ");
            }
        });
    }

    private void initData8() {//zip使用,比如在两个服务器上获取用户的信息，组合起来才是用户完整的信息
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                Log.e(TAG, "onNext1: " + 1);
                e.onNext(1);
                Log.e(TAG, "onNext1: " + 2);
                e.onNext(2);
                Log.e(TAG, "onNext1: " + 3);
                e.onNext(3);
                Log.e(TAG, "onNext1: " + 4);
                e.onNext(4);
            }
        });
        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                Log.e(TAG, "onNext2: " + "A");
                e.onNext("A");
                Log.e(TAG, "onNext2: " + "B");
                e.onNext("B");
                Log.e(TAG, "onNext2: " + "C");
                e.onNext("C");
                Log.e(TAG, "onNext2: " + "D");
                e.onNext("D");
            }
        });
        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(@NonNull Integer integer, @NonNull String s) throws Exception {
                return integer + s;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.e(TAG, "accept: " + s);
            }
        });
    }


    private void initData7() {//concatMap使用和flatMap一样,严格按照顺序执行
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                Log.e(TAG, "subscribe: " + Thread.currentThread().getName());//打印当前线程名称
                Log.e(TAG, "emitter" + 1);
                emitter.onNext(1);
                Log.e(TAG, "emitter" + 2);
                emitter.onNext(2);
                Log.e(TAG, "emitter" + 3);
                emitter.onNext(3);
                emitter.onComplete();
                Log.e(TAG, "emitter" + 4);
                emitter.onNext(4);
            }
        }).concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull Integer integer) throws Exception {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("this is value" + integer);
                }
                return Observable.fromIterable(list).delay(10, TimeUnit.MILLISECONDS);
            }
        }).delay(10, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.e(TAG, "accept: " + s);
                    }
                });
    }


    private void initData6() {//flatmap使用,flatmap不保证顺序，flatmap把新来的上游的事件转换为3个string
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                Log.e(TAG, "subscribe: " + Thread.currentThread().getName());//打印当前线程名称
                Log.e(TAG, "emitter" + 1);
                emitter.onNext(1);
                Log.e(TAG, "emitter" + 2);
                emitter.onNext(2);
                Log.e(TAG, "emitter" + 3);
                emitter.onNext(3);
                emitter.onComplete();
                Log.e(TAG, "emitter" + 4);
                emitter.onNext(4);
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull Integer integer) throws Exception {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("this is value" + integer);
                }
                return Observable.fromIterable(list).delay(10, TimeUnit.MILLISECONDS);
            }
        }).delay(10, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.e(TAG, "accept: " + s);
                    }
                });
    }


    private void initData5() {//map使用
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                Log.e(TAG, "subscribe: " + Thread.currentThread().getName());//打印当前线程名称
                Log.e(TAG, "emitter" + 1);
                emitter.onNext(1);
                Log.e(TAG, "emitter" + 2);
                emitter.onNext(2);
                Log.e(TAG, "emitter" + 3);
                emitter.onNext(3);
                emitter.onComplete();
                Log.e(TAG, "emitter" + 4);
                emitter.onNext(4);
            }
        }).map(new Function<Integer, String>() {//Integet是输入的值，String是输出返回的值
            @Override
            public String apply(@NonNull Integer integer) throws Exception {
                return "this is result" + integer;
            }
        }).subscribeOn(Schedulers.io())//上游开启io线程
                .observeOn(AndroidSchedulers.mainThread())//下游在主线程处理结果
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String string) throws Exception {
                        Log.e(TAG, "accept: " + Thread.currentThread().getName());
                        Log.e(TAG, "result: " + string);
                    }
                });
    }

    private void initData4() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                Log.e(TAG, "subscribe: " + Thread.currentThread().getName());//打印当前线程名称
                Log.e(TAG, "emitter" + 1);
                emitter.onNext(1);
                Log.e(TAG, "emitter" + 2);
                emitter.onNext(2);
                Log.e(TAG, "emitter" + 3);
                emitter.onNext(3);
                emitter.onComplete();
                Log.e(TAG, "emitter" + 4);
                emitter.onNext(4);
            }
        }).subscribeOn(Schedulers.io())//上游开启io线程
                .observeOn(AndroidSchedulers.mainThread())//下游在主线程处理结果
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Log.e(TAG, "accept: " + Thread.currentThread().getName());
                        Log.e(TAG, "accept: " + integer);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                });
    }

    private void initData3() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                Log.e(TAG, "emitter" + 1);
                emitter.onNext(1);
                Log.e(TAG, "emitter" + 2);
                emitter.onNext(2);
                Log.e(TAG, "emitter" + 3);
                emitter.onNext(3);
                emitter.onComplete();
                Log.e(TAG, "emitter" + 4);
                emitter.onNext(4);
            }
        }).subscribeOn(Schedulers.io())//上游开启io线程
                .observeOn(AndroidSchedulers.mainThread())//下游在主线程处理结果
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        Log.e(TAG, "onSubscribe: ");
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(@NonNull Integer integer) {
                        Log.e(TAG, "" + integer);
                        if (integer == 2) {
                            mDisposable.dispose();//切断上下游连通的管道
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "onError: ");
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: ");
                    }
                });
    }

    private void initData2() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                Log.e(TAG, "emitter" + 1);
                emitter.onNext(1);
                Log.e(TAG, "emitter" + 2);
                emitter.onNext(2);
                Log.e(TAG, "emitter" + 3);
                emitter.onNext(3);
                emitter.onComplete();
                Log.e(TAG, "emitter" + 4);
                emitter.onNext(4);
            }
        }).subscribeOn(Schedulers.io())//上游开启io线程
                .observeOn(AndroidSchedulers.mainThread())//下游在主线程处理结果
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.e(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(@NonNull Integer integer) {
                        Log.e(TAG, "" + integer);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "onError: ");
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: ");
                    }
                });
    }

    private void initData1() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                Log.e(TAG, "emitter" + 1);
                emitter.onNext(1);
                Log.e(TAG, "emitter" + 2);
                emitter.onNext(2);
                Log.e(TAG, "emitter" + 3);
                emitter.onNext(3);
                Log.e(TAG, "emitter" + 4);
                emitter.onNext(4);
                Log.e(TAG, "complite");
                emitter.onComplete();
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.e(TAG, "subscribe: ");
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                Log.e(TAG, "onNext: " + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete: ");
            }
        });
    }

    private void initData0() {//Observable,Observer方式使用
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                Log.e(TAG, "emitter" + 1);
                emitter.onNext(1);
                Log.e(TAG, "emitter" + 2);
                emitter.onNext(2);
                Log.e(TAG, "emitter" + 3);
                emitter.onNext(3);
                Log.e(TAG, "emitter" + 4);
                emitter.onNext(4);
                Log.e(TAG, "complite");
                emitter.onComplete();
            }
        });

        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.e(TAG, "subscribe: ");
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                Log.e(TAG, "onNext: " + integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete: ");
            }
        };
        observable.subscribe(observer);
    }
}
