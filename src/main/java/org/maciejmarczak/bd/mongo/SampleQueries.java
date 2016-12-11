package org.maciejmarczak.bd.mongo;

import com.mongodb.DBObject;
import org.maciejmarczak.bd.mongo.model.Question;
import org.maciejmarczak.bd.mongo.util.TerminalColors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Component
class SampleQueries {

    private MongoOperations mongoOps;

    @Autowired
    SampleQueries(MongoOperations mongoOps) {
        this.mongoOps = mongoOps;
    }

    void execAllSamples() {
        simpleQuery();
        queryWithAggregation();
        mapReduceQuery();
    }

    void simpleQuery() {
        System.out.println(TerminalColors.ANSI_BLUE + "SIMPLE QUERY");

        Query query = new BasicQuery("{ 'category': 'HISTORY', 'value': { $regex: '\\\\$([1-2][6-9]|2[0-9])[0-9][0-9]' } }");
        query.with(new Sort(Sort.Direction.DESC, "air_date"));

        List<Question> questions = mongoOps.find(query, Question.class);

        for (Question q : questions) {
            System.out.println(TerminalColors.ANSI_BLUE + "" + q);
        }

        System.out.println(TerminalColors.ANSI_DEFAULT);
    }

    void queryWithAggregation() {
        System.out.println(TerminalColors.ANSI_RED + "AGGREGATION QUERY");

        Aggregation aggregation = newAggregation(
                match(Criteria
                        .where("air_date").gte("2008-01-01")
                        .and("category").is("HISTORY")
                ),
                group("show_number").count().as("total"),
                project("total").and("show_number").previousOperation()
        );

        AggregationResults<DBObject> aggregationResults = mongoOps.aggregate(aggregation, "question", DBObject.class);

        for (DBObject dbObject : aggregationResults.getMappedResults()) {
            System.out.println("SHOW NUMBER: " + dbObject.get("show_number") + " TOTAL: " + dbObject.get("total"));
        }

        System.out.println(TerminalColors.ANSI_DEFAULT);
    }

    void mapReduceQuery() {
        System.out.println(TerminalColors.ANSI_GREEN + "MAP-REDUCE QUERY (NOT ALL RESULTS, THERE IS TOO MANY OF THEM)");

        String mapFunction = "function() {" +
                "var val = (this.value == null) ? null : parseInt(this.value.substring(1));" +
                "emit(this.show_number, val);" +
                "}";

        String reduceFunction = "function(key, values) { return Array.sum(values); }";
        MapReduceResults<DBObject> mapReduceResults = mongoOps.mapReduce("question", mapFunction, reduceFunction, DBObject.class);

        int counter = 0;
        for (DBObject dbObject : mapReduceResults) {
            if (counter % 500 == 0) {
                System.out.println("SHOW NUMBER: " + dbObject.get("_id") + " TOTAL VALUE: " + dbObject.get("value") + "$");
                counter = 0;
            }
            counter++;
        }

        System.out.println(TerminalColors.ANSI_DEFAULT);
    }

}
