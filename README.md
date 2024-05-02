Self-Employment Business API
========================
The Self-Employment Business API allows software packages to:

* to create or amend a self-employment annual summary for a tax year.
* to delete the self-employment annual summary for a tax year.
* to retrieve a self-employment annual summary for a tax year.
* to create a self-employment periodic update for submission of periodic data
* to amend a self-employment periodic update.
* to retrieve a list of all self-employment update periods.
* to retrieve a single self-employment periodic update for a given identifier.

## Requirements

- Scala 2.13.x
- Java 11
- sbt 1.9.7
- [Service Manager](https://github.com/hmrc/service-manager)

## Development Setup

To run the microservice from console, use `sbt run`. (starts on port 7801 by default)

To start the service manager profile: `sm2 --start MTDFB_SELF_EMPLOYMENT_BUSINESS`.

## Run Tests

```
sbt test
sbt it:test
```

## To view the OAS

To view documentation locally ensure the Self-Employment Business API is running, and run api-documentation-frontend:

```
./run_local_with_dependencies.sh
```

Then go to http://localhost:9680/api-documentation/docs/openapi/preview and use this port and version:

```
http://localhost:7801/api/conf/3.0/application.yaml
```

## Changelog

You can see our changelog [here](https://github.com/hmrc/income-tax-mtd-changelog/wiki)

## Support and Reporting Issues

You can create a GitHub issue [here](https://github.com/hmrc/income-tax-mtd-changelog/issues)

## API Reference / Documentation

Available on
the [HMRC Developer Hub](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/self-employment-business-api/1.0)

## License

This code is open source software licensed under
the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
