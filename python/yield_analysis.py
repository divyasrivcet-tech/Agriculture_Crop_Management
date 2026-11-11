"""Basic crop-yield analysis. Export the full yield report as CSV from pgAdmin first."""
from pathlib import Path
import pandas as pd
import matplotlib.pyplot as plt

CSV_FILE = Path(__file__).with_name("yield_report.csv")


def main():
    if not CSV_FILE.exists():
        print("yield_report.csv was not found.")
        print("In pgAdmin, run database/03_useful_queries.sql query 4 and save the result as yield_report.csv in this folder.")
        return
    data = pd.read_csv(CSV_FILE)
    print("\n--- Yield Report ---")
    print(data.head())
    if "yield_kg" not in data.columns:
        print("CSV must include a yield_kg column.")
        return
    values = pd.to_numeric(data["yield_kg"], errors="coerce").dropna()
    if values.empty:
        print("No numeric yield values to analyse.")
        return
    print(f"Average yield: {values.mean():.2f} kg")
    print(f"Highest yield: {values.max():.2f} kg")
    print(f"Lowest yield: {values.min():.2f} kg")
    if "crop_name" in data.columns:
        crop_totals = data.assign(yield_kg=pd.to_numeric(data["yield_kg"], errors="coerce")).groupby("crop_name")["yield_kg"].sum().sort_values(ascending=False)
        print("\nTotal yield by crop:")
        print(crop_totals)
        crop_totals.plot(kind="bar", title="Total Yield by Crop", ylabel="Yield (kg)")
        plt.tight_layout()
        plt.savefig(Path(__file__).with_name("yield_by_crop.png"))
        print("Chart saved as yield_by_crop.png")
        plt.show()


if __name__ == "__main__":
    main()
