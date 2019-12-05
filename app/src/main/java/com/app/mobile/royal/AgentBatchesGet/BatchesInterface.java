package com.app.mobile.royal.AgentBatchesGet;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BatchesInterface {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBatches(Batches... batches);

    @Query("Select * from Batches")
    List<Batches> batchestabledata();

    @Query("Select batches from Batches WHERE batches LIKE:query")
    List<Batches> count(String query);

    @Query("Delete FROM Batches Where batches LIKE:query")
    void deleteBatches(String query);
}
