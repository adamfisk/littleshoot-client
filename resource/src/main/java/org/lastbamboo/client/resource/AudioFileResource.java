package org.lastbamboo.client.resource;


/**
 * Interface for audio file resources.
 */
public interface AudioFileResource extends LocalFileResource 
    {
    
    /**
     * Accessor for the bit rate of the file.
     * 
     * @return the file's bit rate.
     */
    int getBitRate();
    
    /**
     * Sets the bit rate. 
     * 
     * @param bitRate The bitRate to set.
     */
    void setBitRate(final int bitRate);
    
    /**
     * Accessor for the album for this song.
     * 
     * @return the name of the album, or the empty string if the album is unknown.
     */
    String getAlbum();
    
    /**
     * Mutator for the album name.
     *
     * @param album The album name to set.
     */
    void setAlbum(final String album);
    
    /**
     * Accessor for the genre for this song.
     * 
     * @return the genre of the song, or the empty string if the genre is unknown.
     */
    String getGenre();
    
    /**
     * Mutator for the genre.
     * 
     * @param genre The genre to set.
     */
    void setGenre(final String genre);
    
    /**
     * Accessor for the year this song was released.
     * 
     * @return the year the song was released.
     */
    int getYear();
    
    /**
     * Mutator for the year the work was produced.
     * 
     * @param year The year to set.
     */
    void setYear(final int year);

    /**
     * Accessor for any comment on this audio file.
     * 
     * @return The string comment for this file.
     */
    String getComment();

    /**
     * Mutator for the comment field.
     * 
     * @param comment The comment to set.
     */
    void setComment(final String comment);

    }
