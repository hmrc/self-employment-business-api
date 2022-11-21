<p>Scenario simulations using Gov-Test-Scenario headers is only available in the sandbox environment.</p>
<table>
    <thead>
        <tr>
            <th>Header Value (Gov-Test-Scenario)</th>
            <th>Scenario</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td><p>N/A - DEFAULT</p></td>
            <td><p>Simulates success response.</p></td>
        </tr>
        <tr>
            <td><p>OVERLAPPING_PERIOD</p></td>
            <td><p>Simulates the scenario where the period summary overlaps with an existing period summary.</p></td>
        </tr>
        <tr>
            <td><p>MISALIGNED_PERIOD</p></td>
            <td><p>Simulates the scenario where the period summary isn't within the accounting period.</p></td>
        </tr>
        <tr>
            <td><p>NOT_CONTIGUOUS_PERIOD</p></td>
            <td><p>Simulates the scenario where the period summaries are not contiguous.</p></td>
        </tr>
        <tr>
            <td><p>NOT_ALLOWED_CONSOLIDATED_EXPENSES</p></td>
            <td><p>Simulates the scenario where the cumulative turnover amount exceeds the consolidated expenses threshold.</p></td>
        </tr>
        <tr>
            <td><p>DUPLICATE_SUBMISSION</p></td>
            <td><p>Simulates the scenario where a summary has already been submitted for the specified period.</p>
            <p>This scenario only occurs for data related to tax years from 2023-24 onwards. </p></td>
        </tr>
        <tr>
            <td><p>TAX_YEAR_NOT_SUPPORTED</p></td>
            <td><p>Simulates the scenario where the tax year is not supported. </p>
            <p>This scenario only occurs for data related to tax years from 2023-24 onwards. </p></td>
        </tr>
        <tr>
            <td><p>NOT_FOUND</p></td>
            <td><p>Simulates the scenario where no data is found.</p></td>
        </tr>
    </tbody>
</table>
