package org.lastbamboo.client.services.download;

import org.json.JSONObject;
import org.lastbamboo.common.download.MoverDState;
import org.lastbamboo.common.download.MsDState;
import org.lastbamboo.common.download.Sha1DState;
import org.lastbamboo.common.download.MoverDState.MoveFailed;
import org.lastbamboo.common.download.MoverDState.Moved;
import org.lastbamboo.common.download.MoverDState.MovedToITunes;
import org.lastbamboo.common.download.MoverDState.Moving;
import org.lastbamboo.common.download.Sha1DState.Sha1Mismatch;
import org.lastbamboo.common.download.Sha1DState.VerifiedSha1;
import org.lastbamboo.common.download.Sha1DState.VerifyingSha1;
import org.lastbamboo.common.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Visitor for turning download data into JSON.
 */
public class MoverDStateStatusVisitor implements 
    MoverDState.Visitor<String,Sha1DState<MsDState>>
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    private static final int MOVING_STATE = 200;
    private static final int MOVED_STATE = 201;
    private static final int MOVE_FAILED_STATE = 202;
    private static final int MOVED_TO_ITUNES_STATE = 203;
    
    /**
     * Constant string for the download status.
     */
    public static final String DOWNLOAD_STATUS = "downloadStatus";
    
    
    private static final int IDLE_STATE = 0;
    private static final int GETTING_SOURCES_STATE = 1;
    private static final int DOWNLOADING_STATE = 2;
    private static final int NO_SOURCES_STATE = 3;
    private static final int COULD_NOT_DETERMINE_SOURCES_STATE = 4;
    private static final int CANCELED_STATE = 5;
    public static final int COMPLETE_STATE = 6;
    private static final int FAILED_STATE = 7;
    private static final int PAUSED_STATE = 8;
    
    private final JSONObject m_json;

    /**
     * Creates a new visitor.
     * 
     * @param json The JSON object to add data to.
     */
    public MoverDStateStatusVisitor(final JSONObject json)
        {
        this.m_json = json;
        }

    public String visitDownloading(
        final MoverDState.Downloading<Sha1DState<MsDState>> state)
        {
        //m_log.debug("Visiting state: {}", state);
        final Sha1DState<MsDState> delegateState = state.getDelegateState();
        //m_log.debug("Visiting downloading state: {}", delegateState);
        return delegateState.accept(
            new Sha1StateStatusVisitor (this.m_json));
        }
    
    public String visitFailed(
        final MoverDState.Failed<Sha1DState<MsDState>> state)
        {
        //m_log.debug("Visiting state: {}", state);
        final Sha1DState<MsDState> delegateState = state.getDelegateState();
        //m_log.debug("Visiting downloading state: {}", delegateState);
        return delegateState.accept(
            new Sha1StateStatusVisitor (this.m_json));
        }

    public String visitMoving(final Moving<Sha1DState<MsDState>> state)
        {
        m_log.debug("Visiting state: {}", state);
        JsonUtils.put(this.m_json, DOWNLOAD_STATUS, MOVING_STATE);
        return  null;
        }

    public String visitMoved (final Moved<Sha1DState<MsDState>> state)
        {
        //m_log.debug("Visiting state: {}", state);
        JsonUtils.put(this.m_json, DOWNLOAD_STATUS, MOVED_STATE);
        //final MsDState downoadState = state.getDownloadState();
        return  null;
        }
    
    public String visitMovedToITunes (
        final MovedToITunes<Sha1DState<MsDState>> state)
        {
        //m_log.debug("Visiting state: {}", state);
        JsonUtils.put(this.m_json, DOWNLOAD_STATUS, MOVED_TO_ITUNES_STATE);
        return  null;
        }

    public String visitMoveFailed(
        final MoveFailed<Sha1DState<MsDState>> state)
        {
        //m_log.debug("Visiting state: {}", state);
        JsonUtils.put(this.m_json, DOWNLOAD_STATUS, MOVE_FAILED_STATE);
        return  null;
        }
    
    /**
     * A visitor for a multi-source downloader that returns a string
     * representing the state.
     */
    private static class MsDStateStatusVisitor 
        implements MsDState.Visitor<String>
        {

        private final Logger m_log = LoggerFactory.getLogger(getClass());
        
        private final JSONObject m_json;

        private MsDStateStatusVisitor(final JSONObject json)
            {
            //m_log.debug("Creating new downloading status visitor...");
            this.m_json = json;
            }

        public String visitCanceled (final MsDState.Canceled state)
            {
            JsonUtils.put(this.m_json, DOWNLOAD_STATUS, CANCELED_STATE);
            return null;
            }

        public String visitFailed (final MsDState.Failed state)
            {
            m_log.debug("Visiting failed...");
            JsonUtils.put(this.m_json, DOWNLOAD_STATUS, FAILED_STATE);
            return null;
            }
        
        public String visitComplete (final MsDState.Complete state)
            {
            JsonUtils.put(this.m_json, DOWNLOAD_STATUS, COMPLETE_STATE);
            return null;
            }

        public String visitCouldNotDetermineSources(
            final MsDState.CouldNotDetermineSources state)
            {
            JsonUtils.put(this.m_json, DOWNLOAD_STATUS, 
                COULD_NOT_DETERMINE_SOURCES_STATE);
            return null;
            }


        public String visitPaused(final MsDState.Paused state)
            {
            final MsDState.Downloading dlState = state.getDownloadingState();
            dlState.accept(this);
            
            // Now just overwrite the state!
            JsonUtils.put(this.m_json, DOWNLOAD_STATUS, PAUSED_STATE);
            return null;
            }
        
        // This is the base downloading method all others call.
        public String visitDownloading (final MsDState.Downloading state)
            {
            final int numSources = state.getNumSources ();
            JsonUtils.put(this.m_json, "downloadNumSources", numSources);
            JsonUtils.put(this.m_json, "downloadSpeed", state.getKbs());
            JsonUtils.put(this.m_json, "downloadBytesRead", state.getBytesRead());
            JsonUtils.put(this.m_json, "timeRemaining", state.getTimeRemaining());
            JsonUtils.put(this.m_json, DOWNLOAD_STATUS, DOWNLOADING_STATE);
            
            return null;
            }

        public String visitLittleShootDownloading(
            final MsDState.LittleShootDownloading state)
            {
            // Add base state data.
            visitDownloading(state);
            JsonUtils.put(this.m_json, "downloadSource", 0);
            return null;
            }
    
        public String visitLimeWireDownloading(
            final MsDState.LimeWireDownloading state)
            {
            // Add base state data.
            visitDownloading(state);
            JsonUtils.put(this.m_json, "downloadSource", 1);
            return null;
            }
    
        public String visitLibTorrentDownloading(
            final MsDState.LibTorrentDownloading state)
            {
            // Add base state data.
            visitDownloading(state);
            JsonUtils.put(this.m_json, "maxByte", state.getMaxContiguousByte());
            JsonUtils.put(this.m_json, "numFiles", state.getNumFiles());
            JsonUtils.put(this.m_json, "downloadSource", 2);
            return null;
            }

        public String visitGettingSources (final MsDState.GettingSources state)
            {
            JsonUtils.put(this.m_json, DOWNLOAD_STATUS, GETTING_SOURCES_STATE);
            return null;
            }

        public String visitIdle (final MsDState.Idle state)
            {
            JsonUtils.put(this.m_json, DOWNLOAD_STATUS, IDLE_STATE);
            return null;
            }

        public String visitNoSourcesAvailable (
            final MsDState.NoSourcesAvailable state)
            {
            JsonUtils.put(this.m_json, DOWNLOAD_STATUS, NO_SOURCES_STATE);
            return null;
            }
        }
    
    /**
     * A visitor for a SHA-1 verifying downloader that returns a string
     * representing the state.
     */
    private static class Sha1StateStatusVisitor
        implements Sha1DState.Visitor<String,MsDState>
        {
        private static final int VERIFYING_SHA1_STATE = 100;
        private static final int SHA1_MISMATCH_STATE = 101;
        private static final int SHA1_VERIFIED_STATE = 102;
        
        private final JSONObject m_json;

        private Sha1StateStatusVisitor(final JSONObject json)
            {
            this.m_json = json;
            }

        public String visitDownloading (
            final Sha1DState.Downloading<MsDState> state)
            {
            //m_log.debug("Visiting state: {}", state);
            final MsDState delegateState = state.getDelegateState();
            //m_log.debug("Forwarding to delegate state: {}", delegateState);
            return delegateState.accept(
                new MsDStateStatusVisitor (this.m_json));
            }

        public String visitFailed (
            final Sha1DState.Failed<MsDState> state)
            {
            //m_log.debug("Visiting state: {}", state);
            final MsDState delegateState = state.getDelegateState();
            //m_log.debug("Forwarding to delegate state: {}", delegateState);
            return delegateState.accept(
                new MsDStateStatusVisitor (this.m_json));
            }
    
        public String visitSha1Mismatch(final Sha1Mismatch<MsDState> state)
            {
            JsonUtils.put(this.m_json, DOWNLOAD_STATUS, SHA1_MISMATCH_STATE);
            return null;
            }

        public String visitVerifiedSha1(final VerifiedSha1<MsDState> state)
            {
            JsonUtils.put(this.m_json, DOWNLOAD_STATUS, SHA1_VERIFIED_STATE);
            return null;
            }

        public String visitVerifyingSha1(final VerifyingSha1<MsDState> state)
            {
            JsonUtils.put(this.m_json, DOWNLOAD_STATUS, VERIFYING_SHA1_STATE);
            return null;
            }
        }
    }
