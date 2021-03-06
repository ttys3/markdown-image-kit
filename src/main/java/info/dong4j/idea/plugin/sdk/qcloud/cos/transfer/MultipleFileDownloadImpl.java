package info.dong4j.idea.plugin.sdk.qcloud.cos.transfer;

import info.dong4j.idea.plugin.sdk.qcloud.cos.event.ProgressListenerChain;
import info.dong4j.idea.plugin.sdk.qcloud.cos.exception.CosClientException;
import info.dong4j.idea.plugin.sdk.qcloud.cos.exception.CosServiceException;

import java.io.*;
import java.util.Collection;

/**
 * Multiple file download when downloading an entire virtual directory.
 */
public class MultipleFileDownloadImpl extends MultipleFileTransfer<Download> implements MultipleFileDownload {

    private final String keyPrefix;
    private final String bucketName;

    public MultipleFileDownloadImpl(String description, TransferProgress transferProgress,
            ProgressListenerChain progressListenerChain, String keyPrefix, String bucketName, Collection<? extends Download> downloads) {
        super(description, transferProgress, progressListenerChain, downloads);
        this.keyPrefix = keyPrefix;
        this.bucketName = bucketName;
    }

    /**
     * Returns the key prefix of the virtual directory being downloaded.
     */
    public String getKeyPrefix() {
        return keyPrefix;
    }

    /**
     * Returns the name of the bucket from which files are downloaded.
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Waits for this transfer to complete. This is a blocking call; the current
     * thread is suspended until this transfer completes.
     *
     * @throws CosClientException
     *             If any errors were encountered in the client while making the
     *             request or handling the response.
     * @throws CosServiceException
     *             If any errors occurred in Qcloud COS while processing the
     *             request.
     * @throws InterruptedException
     *             If this thread is interrupted while waiting for the transfer
     *             to complete.
     */
    @Override
    public void waitForCompletion()
            throws CosClientException, CosServiceException, InterruptedException {
        if (subTransfers.isEmpty())
            return;
        super.waitForCompletion();
    }

    /**
     * Aborts all outstanding downloads.
     */
    public void abort() throws IOException {
        /*
         * The abort() method of DownloadImpl would attempt to notify its
         * TransferStateChangeListener BEFORE it releases its intrinsic lock.
         * And according to the implementation of
         * MultipleFileTransferStateChangeListener which is actually shared by
         * all sub-transfers, it will call the synchronized method isDone() on
         * ALL sub-transfer objects. This would result in serious
         * contention with the worker threads who try to acquire the same set of
         * locks to call setState().
         * In order to prevent this. we should first cancel all download jobs and
         * then notify the listener.
         */

        /* First abort all the download jobs without notifying the state change listener.*/
        for (Transfer fileDownload : subTransfers) {
            ((DownloadImpl)fileDownload).abortWithoutNotifyingStateChangeListener();
        }

        /*
         * All sub-transfers are already in CANCELED state. Now the main thread
         * is able to check isDone() on each sub-transfer object without
         * contention with worker threads.
         */
        for (Transfer fileDownload : subTransfers) {
            ((DownloadImpl)fileDownload).notifyStateChangeListeners(TransferState.Canceled);
        }
    }
}
